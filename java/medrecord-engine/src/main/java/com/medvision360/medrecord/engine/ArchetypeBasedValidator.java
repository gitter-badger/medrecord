/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.medvision360.medrecord.rmutil.AOMUtil;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.base.BaseValidationReport;
import com.medvision360.medrecord.spi.base.BaseValidationResult;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.constraintmodel.ArchetypeConstraint;
import org.openehr.am.archetype.constraintmodel.ArchetypeInternalRef;
import org.openehr.am.archetype.constraintmodel.ArchetypeSlot;
import org.openehr.am.archetype.constraintmodel.CAttribute;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.am.archetype.constraintmodel.CDomainType;
import org.openehr.am.archetype.constraintmodel.CMultipleAttribute;
import org.openehr.am.archetype.constraintmodel.CObject;
import org.openehr.am.archetype.constraintmodel.CPrimitiveObject;
import org.openehr.am.archetype.constraintmodel.CSingleAttribute;
import org.openehr.am.archetype.constraintmodel.ConstraintRef;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.ObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchetypeBasedValidator extends AOMUtil implements LocatableValidator
{
    private ArchetypeStore m_archetypeStore;

    public ArchetypeBasedValidator(ArchetypeStore archetypeStore, Map<SystemValue, Object> systemValues)
    {
        super(systemValues);
        m_archetypeStore = archetypeStore;
    }

    @Override
    public void check(Locatable value) throws ValidationException, NotSupportedException
    {
        checkNotNull(value, "value cannot be null");
        ValidationReport validationReport = validate(value);
        if (!validationReport.isValid())
        {
            throw new ValidationException(validationReport);
        }
    }

    @Override
    public boolean supports(Locatable test)
    {
        checkNotNull(test, "test cannot be null");
        return supports(test.getArchetypeDetails());
    }

    @Override
    public boolean supports(Archetyped test)
    {
        checkNotNull(test, "test cannot be null");
        try
        {
            return m_archetypeStore.has(test);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public ValidationReport validate(Locatable value) throws NotSupportedException
    {
        checkNotNull(value, "value cannot be null");
        Archetyped archetypeDetails = value.getArchetypeDetails();
        if (archetypeDetails == null)
        {
            throw new NotSupportedException(String.format(
                    "cannot validate locatable %s, it does not have an archetype", value.getUid()));
        }
        checkSupport(value);

        BaseValidationReport validationReport = new BaseValidationReport();
        BaseValidationResult result = validationReport.newResult("/");

        ArchetypeID archetypeID = archetypeDetails.getArchetypeId();
        Archetype archetype;
        try
        {
            WrappedArchetype wrappedArchetype = m_archetypeStore.get(archetypeID);
            archetype = wrappedArchetype.getArchetype();
            result.setMessage(report("recognized archetype", archetypeID.getValue()));
        }
        catch (NotFoundException | IOException | ParseException e)
        {
            result.setMessage(report("cannot find archetype", archetypeID.getValue(), e));
            result.setValid(false);
            result.setDetails(e);
            return validationReport;
        }
        
        CComplexObject complexObject = archetype.getDefinition();
        validateComplexObject(validationReport, archetype, value, complexObject);

        return validationReport;
    }

    private void validateComplexObject(BaseValidationReport validationReport, Archetype archetype, Locatable value,
            CComplexObject complexObject)
    {
        validateRmEntity(validationReport, value, complexObject);

        String nodeId = complexObject.getNodeId();
        validateNodeId(validationReport, complexObject, value, nodeId);

        List<CAttribute> attributes = complexObject.getAttributes();
        for (CAttribute attribute : attributes)
        {
            validateAttribute(validationReport, archetype, value, attribute);
        }
    }

    private void validateAttribute(BaseValidationReport validationReport, Archetype archetype, Locatable value,
            CAttribute attribute)
    {
        UIDBasedID uid = value.getUid();

        String attributeName = attribute.getRmAttributeName();
        String path = attribute.path();
        CAttribute.Existence existence = attribute.getExistence();

        Object attributeValue;
        try
        {
            attributeValue = get(value, attributeName);
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            BaseValidationResult result = validationReport.newResult(path);
            result.setMessage(report("cannot retrieve value", attribute, uid, archetype, e));
            result.setValid(false);
            result.setDetails(e);
            return;
        }
        switch (existence)
        {
            case NOT_ALLOWED:
                if (nullOrEmpty(attributeValue))
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setMessage(report("value not allowed is was not set", attribute, uid, archetype));
                }
                return;
            case OPTIONAL:
                if (nullOrEmpty(attributeValue))
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setMessage(report("value optional and is not set", attribute, uid, archetype));
                    return;
                }
                break;
            case REQUIRED:
            default:
                if (attributeValue == null)
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setValid(false);
                    result.setMessage(report("value required but is not set", attribute, uid, archetype));
                    return;
                }
                break;
        }

        validateChildren(validationReport, archetype, value, attribute, attributeValue);
    }

    private void validateChildren(BaseValidationReport validationReport, Archetype archetype, Locatable parent, 
            CAttribute attribute, Object value)
    {
        // value != null, attribute.isAllowed

        String path = attribute.path();
        List<CObject> children = attribute.getChildren();
        if (children.size() == 0)
        {
            return;
        }
        if (attribute instanceof CSingleAttribute)
        {
            // todo deal with forceMultiple
            handleSingleAttribute(validationReport, archetype, parent, attribute, value);
        }
        else if (attribute instanceof CMultipleAttribute)
        {
            CMultipleAttribute multipleAttribute = (CMultipleAttribute) attribute;
            handleMultipleAttribute(validationReport, archetype, parent, multipleAttribute, value);
        }
        else
        {
            BaseValidationResult result = validationReport.newResult(path);
            result.setMessage(report("unrecognized attribute", attribute.getClass(), attribute, parent, archetype));
            result.setValid(false);
            return;
        }
    }

    private void handleSingleAttribute(BaseValidationReport validationReport, Archetype archetype, Locatable parent,
            CAttribute attribute, Object value)
    {
        CSingleAttribute singleAttribute = (CSingleAttribute) attribute;
        List<CObject> alternatives = singleAttribute.alternatives();

        CObject alternative = validateNodeId(validationReport, archetype, parent, attribute, value, alternatives);
        if (alternative != null)
        {
            boolean validRmEntity = validateRmEntity(validationReport, parent, archetype, attribute, value, 
                    alternative);
            if (validRmEntity)
            {
                validateObject(validationReport, archetype, alternative, value);
            }
        }
    }

    private void handleMultipleAttribute(BaseValidationReport validationReport, Archetype archetype, Locatable parent,
            CMultipleAttribute attribute, Object value)
    {
        if (value instanceof Collection<?>)
        {
            List<Object> matchedChildren = new ArrayList<>();
            Map<Object, CObject> validChildren = new HashMap<>();

            // check all constraints, putting all children visited into matchedChildren and all
            // children that look valid into validChildren
            validCollectionAgainstConstraints(validationReport, archetype, parent, attribute,
                    (Collection<?>) value, matchedChildren, validChildren);

            // check all children, reporting any unvisited children and recursing into all valid
            // children to check them further
            validateChildCollection(validationReport, archetype, parent, attribute, (Collection<?>) value,
                    matchedChildren, validChildren);
        }
        else
        {
            // todo
        }
    }

    private void validCollectionAgainstConstraints(BaseValidationReport validationReport, Archetype archetype,
            Locatable parent, CMultipleAttribute attribute, Collection<?> collection, List<Object> matchedChildren,
            Map<Object, CObject> validChildren)
    {
        List<CObject> members = attribute.members();
        for (CObject constraint : members)
        {
            validateCollectionAgainstConstraint(validationReport, archetype, parent, collection, matchedChildren,
                    validChildren,
                    constraint);
        }
    }

    private void validateCollectionAgainstConstraint(BaseValidationReport validationReport, Archetype archetype,
            Locatable parent, Collection<?> collection, List<Object> matchedChildren,
            Map<Object, CObject> validChildren, CObject constraint)
    {
        int occurred = 0;

        String nodeId = constraint.getNodeId();
        for (Object child : collection)
        {
            // check this child, putting it into matchedChildren if it matches, and putting it into validChildren if 
            // it looks ok, and reporting on any violations
            boolean matched = validateChildAgainstConstraint(validationReport, archetype, parent, matchedChildren, 
                validChildren, constraint, occurred, nodeId, child);
            if (matched)
            {
                occurred++;
            }
        }
        
        if (occurred > 0)
        {
            if (constraint.isAllowed())
            {
                BaseValidationResult result = validationReport.newResult(constraint.path());
                result.setMessage(report("child found", constraint, parent, archetype));
            }
            // else we reported the offending children already
        }
        else if (constraint.isRequired())
        {
            BaseValidationResult result = validationReport.newResult(constraint.path());
            result.setValid(false);
            result.setMessage(report("required child missing", constraint, parent, archetype));
        }
        else
        {
            if (constraint.isAllowed())
            {
                BaseValidationResult result = validationReport.newResult(constraint.path());
                result.setMessage(report("no optional child", constraint, parent, archetype));
            }
            else
            {
                BaseValidationResult result = validationReport.newResult(constraint.path());
                result.setMessage(report("no forbidden child", constraint, parent, archetype));
            }
        }
    }

    private boolean validateChildAgainstConstraint(BaseValidationReport validationReport, Archetype archetype,
            Locatable parent, List<Object> matchedChildren, Map<Object, CObject> validChildren, CObject constraint,
            int occurredAlready, String nodeId, Object child)
    {
        try
        {
            if (!matchNodeId(nodeId, child))
            {
                return false;
            }
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            return false;
        }

        // this constraint applies to this child
        matchedChildren.add(child);

        if (!constraint.isAllowed())
        {
            BaseValidationResult result = validationReport.newResult(constraint.path());
            result.setValid(false);
            result.setMessage(report("forbidden child found",
                    constraint, parent, archetype));
            return true;
        }
        else if (!instanceOf(child, constraint))
        {
            BaseValidationResult result = validationReport.newResult(constraint.path());
            result.setValid(false);
            result.setMessage(report("forbidden child type found",
                    child.getClass(), constraint, parent, archetype));
            return true;
        }
        else if (!constraint.getOccurrences().isUpperUnbounded())
        {
            Interval<Integer> occurrences = constraint.getOccurrences();
            int limit = occurrences.getUpper(); // say, 1
            if(occurrences.isUpperIncluded())
            {
                limit++;                        // say, 2
            }
            if (occurredAlready + 1 >= limit)   // say (1 >= 2) == false
            {
                BaseValidationResult result = validationReport.newResult(constraint.path());
                result.setValid(false);
                if (limit == 2)
                {
                    result.setMessage(report("only one child allowed",
                            constraint, parent, archetype));
                }
                else
                {
                    result.setMessage(report("only", limit - 1, "children allowed",
                            constraint, parent, archetype));
                }
                return true;
            }
        }
        // else unbounded number of kids, or not at limit, good good
        validChildren.put(child, constraint);
        return true;
    }

    private void validateChildCollection(BaseValidationReport validationReport, Archetype archetype, Locatable parent,
            CMultipleAttribute attribute, Collection<?> collection, List<Object> matchedChildren,
            Map<Object, CObject> validChildren)
    {
        // recurse for checked children, report any unchecked children
        for (Object child : collection)
        {
            if (validChildren.containsKey(child))
            {
                CObject constraint = validChildren.get(child);
                validateObject(validationReport, archetype, constraint, child); // recurse!
            }
            else if (matchedChildren.contains(child))
            {
                // already reported as violating the constraint
            }
            else
            {
                // does not match any constraint, report it
                BaseValidationResult result = validationReport.newResult(attribute.path());
                
                if (child instanceof Locatable)
                {
                    Locatable locatableChild = (Locatable) child;
                    if (locatableChild.getArchetypeDetails() != null)
                    {
                        // todo handle slotted children
                        result.setValid(true);
                        result.setMessage(report("todo archetyped child",
                                locatableChild, locatableChild.getArchetypeDetails(),
                                "is a child", attribute, parent, archetype));
                    }
                    else
                    {
                        result.setValid(false);
                        if (locatableChild.getUid() == null)
                        {
                            result.setMessage(report("illegal child", locatableChild.getClass(),
                                    "is a child", attribute, parent, archetype));
                        }
                        else
                        {
                            result.setMessage(report("illegal child", locatableChild,
                                    "is a child", attribute, parent, archetype));
                        }
                    }
                }
                else
                {
                    result.setValid(false);
                    result.setMessage(report("illegal child", child.getClass(), attribute, parent, archetype));
                }
            }
        }
    }

    private boolean matchNodeId(String nodeId, Object child) throws InvocationTargetException, IllegalAccessException
    {
        boolean match = false;
        Object childNodeIdObj = get(child, "archetypeNodeId");
        if (childNodeIdObj == null)
        {
            if (nodeId == null)
            {
                match = true;
            }
        }
        else if (childNodeIdObj instanceof String)
        {
            String childNodeId = (String) childNodeIdObj;
            if (childNodeId.equals(nodeId))
            {
                match = true;
            }
        }
        return match;
    }

    private CObject validateNodeId(BaseValidationReport validationReport, Archetype archetype, Locatable parent,
            CAttribute attribute, Object value, List<CObject> alternatives)
    {
        CObject matchedAlternative = null;
        Set<String> allowedNodeIds = new HashSet<>();

        String locatableNodeId = null;
        try
        {
            Object locatableNodeIdObj = get(value, "archetypeNodeId");
            locatableNodeId = String.valueOf(locatableNodeIdObj);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
        }

        for (CObject alternative : alternatives)
        {
            if (!alternative.isAllowed())
            {
                continue;
            }
            
            String nodeId = alternative.getNodeId();
            if (nodeId != null && !"".equals(nodeId))
            {
                allowedNodeIds.add(nodeId);
            }
            if (locatableNodeId == null)
            {
                continue;
            }
            if (locatableNodeId.equals(nodeId))
            {
                matchedAlternative = alternative;
            }
        }

        BaseValidationResult result = validationReport.newResult(attribute.path());
        if (matchedAlternative != null)
        {
            result.setMessage(report("allowed nodeId", locatableNodeId, parent, archetype));
        }
        else if (allowedNodeIds.size() == 0)
        {
            if (locatableNodeId != null)
            {
                result.setMessage(report("unrestricted nodeId", locatableNodeId, attribute, parent, archetype));
            }
        }
        else if (locatableNodeId == null)
        {
            result.setValid(false);
            result.setMessage(report("nodeId is null but should be",
                    allowedNodeIds, attribute, parent, archetype));
        }
        else
        {
            result.setValid(false);
            result.setMessage(report("nodeId is", locatableNodeId, "but should be",
                    allowedNodeIds, attribute, parent, archetype));
        }
        return matchedAlternative;
    }

    private void validateNodeId(BaseValidationReport validationReport, ArchetypeConstraint constraint, Locatable value,
            String nodeId)
    {
        if (nodeId == null)
        {
            return;
        }
        BaseValidationResult result = validationReport.newResult(constraint.path());

        String locatableNodeId = value.getArchetypeNodeId();
        if (nodeId.equals(locatableNodeId))
        {
            result.setMessage(report("allowed nodeId",
                    locatableNodeId, constraint, value, value.getArchetypeDetails()));
        }
        else
        {
            result.setValid(false);
            result.setMessage(report("nodeId is", locatableNodeId, "but should be",
                    nodeId, constraint, value, value.getArchetypeDetails()));
        }
    }

    private boolean validateRmEntity(BaseValidationReport validationReport, Locatable parent, Archetype archetype,
            CAttribute attribute, Object value, CObject type)
    {
        BaseValidationResult result = validationReport.newResult(attribute.path());
        String rmEntity = type.getRmTypeName();
        if (instanceOf(value, type))
        {
            result.setMessage(report("allowed", value.getClass(), attribute, parent, archetype)); 
            return true;
        }
        else
        {
            result.setValid(false);
            result.setMessage(report("", value.getClass(), "should be",
                    rmEntity, attribute, parent, archetype)); 
            return false;
        }
    }

    private void validateRmEntity(BaseValidationReport validationReport, Locatable value, CObject type)
    {
        BaseValidationResult result = validationReport.newResult(type.path());
        String rmEntity = type.getRmTypeName();

        if (instanceOf(value, rmEntity))
        {
            result.setMessage(report("allowed",
                    value.getClass(), type, value, value.getArchetypeDetails())); 
        }
        else
        {
            result.setValid(false);
            result.setMessage(report("", value.getClass(), "should be",
                    rmEntity, type, value, value.getArchetypeDetails())); 
        }
    }

    private void validateObject(BaseValidationReport validationReport, Archetype archetype, CObject constraint, 
            Object value)
    {
        // nodeId && rmEntity match, constraint != null, value != null, constraint.isAllowed == true
        String archetypeId = archetype.getArchetypeId().getValue();
        String path = constraint.path();
        String nodeId = constraint.getNodeId();

        // todo validate object
        if (constraint instanceof ConstraintRef)
        {
            ConstraintRef constraintRef = (ConstraintRef) constraint;
        }
        else if (constraint instanceof ArchetypeInternalRef)
        {
            ArchetypeInternalRef archetypeInternalRef = (ArchetypeInternalRef) constraint;
        }
        else if (constraint instanceof ArchetypeSlot)
        {
            ArchetypeSlot archetypeSlot = (ArchetypeSlot) constraint;
        }
        else if (constraint instanceof CDomainType)
        {
            CDomainType<?> domainType = (CDomainType<?>) constraint;
        }
        else if (constraint instanceof CComplexObject)
        {
            CComplexObject complexObject = (CComplexObject) constraint;
            if (value instanceof Locatable)
            {
                Locatable locatable = (Locatable) value;
                validateComplexObject(validationReport, archetype, locatable, complexObject);
            }
        }
        else if (constraint instanceof CPrimitiveObject)
        {
            CPrimitiveObject primitiveObject = (CPrimitiveObject) constraint;
        }
        else
        {
            BaseValidationResult result = validationReport.newResult(path);
            result.setValid(false);
            result.setMessage(String.format(
                    "constraint at path %s with nodeId %s has unrecognized type %s for archetype %s",
                    path, nodeId, constraint.getClass().getSimpleName(), archetypeId));
        }
    }

    private boolean instanceOf(Object value, String rmEntity)
    {
        if (value == null || rmEntity == null)
        {
            return value == null && rmEntity == null;
        }

        rmEntity = toClassName(rmEntity);
        String valueClassStr = value.getClass().getSimpleName();
        if (valueClassStr.equals(rmEntity))
        {
            return true;
        }
        Class<?> rmType;
        try
        {
            rmType = retrieveRMType(rmEntity);
        }
        catch (RMObjectBuildingException e)
        {
            return false;
        }

        return rmType.isInstance(value);
    }

    private boolean nullOrEmpty(Object value)
    {
        return value == null || (value instanceof Collection && ((Collection) value).isEmpty());
    }

    private void checkSupport(Locatable locatable) throws NotSupportedException
    {
        if (!supports(locatable))
        {
            throw new NotSupportedException(String.format(
                    "Locatable not supported, unrecognized archetype %s",
                    locatable == null ? null : locatable.getArchetypeDetails().getArchetypeId().getValue()));
        }
    }

    private String report(String report, Object... nouns)
    {
        StringBuilder result = new StringBuilder(report);
        for (Object noun : nouns)
        {
            if (noun == null)
            {
                continue;
            }
            if (noun instanceof Archetype)
            {
                result.append(reportPhrase((Archetype)noun));
            }
            else if (noun instanceof ArchetypeID)
            {
                result.append(reportPhrase((ArchetypeID)noun));
            }
            else if (noun instanceof Archetyped)
            {
                result.append(reportPhrase((Archetyped)noun));
            }
            else if (noun instanceof ObjectID)
            {
                result.append(reportPhrase((ObjectID)noun));
            }
            else if (noun instanceof CAttribute)
            {
                result.append(reportPhrase((CAttribute)noun));
            }
            else if (noun instanceof ArchetypeConstraint)
            {
                result.append(reportPhrase((ArchetypeConstraint)noun));
            }
            else if (noun instanceof Throwable)
            {
                result.append(reportPhrase((Throwable)noun));
            }
            else if (noun instanceof Class<?>)
            {
                result.append(reportPhrase((Class<?>)noun));
            }
            else if (noun instanceof Locatable)
            {
                result.append(reportPhrase((Locatable)noun));
            }
            else if (noun instanceof Collection<?>)
            {
                result.append(reportPhrase((Collection<?>)noun));
            }
//            else if (noun instanceof )
//            {
//                result.append(reportPhrase(()noun));
//            }
            else
            {
                result.append(" ");
                result.append(String.valueOf(noun));
            }
        }
        return result.toString();
    }

    private String reportPhrase(Archetype archetype)
    {
        return reportPhrase(archetype.getArchetypeId());
    }
    
    private String reportPhrase(ArchetypeID archetypeID)
    {
        return reportArchetypePhrase(archetypeID.getValue());
    }

    private String reportPhrase(Archetyped archetyped)
    {
        return reportPhrase(archetyped.getArchetypeId());
    }

    private String reportArchetypePhrase(String value)
    {
        return " of archetype <" + value + ">";
    }

    private String reportPhrase(ObjectID id)
    {
        return " for " + id;
    }

    private String reportPhrase(CAttribute attribute)
    {
        String path = attribute.path();
        if (path == null)
        {
            return "";
        }
        return " at path " + attribute.path();
    }
    
    private String reportPhrase(ArchetypeConstraint constraint)
    {
        String path = constraint.path();
        if (path == null)
        {
            return "";
        }
        return " at path " + constraint.path();
    }
    
    private String reportPhrase(Throwable throwable)
    {
        String message = throwable.getMessage();
        if (message == null)
        {
            return ": " + throwable.getClass().getSimpleName();
        }
        return ": " + throwable.getMessage();
    }

    private String reportPhrase(Class<?> klass)
    {
        return " type {" + klass.getSimpleName() + "}";
    }

    private String reportPhrase(Locatable locatable)
    {
        ObjectID id = locatable.getUid();
        if (id == null)
        {
            return "";
        }
        return reportPhrase(locatable.getUid());
    }

    private String reportPhrase(Collection<?> options)
    {
        if (options.size() == 1)
        {
            return " \"" + String.valueOf(options.iterator().next()) + "\"";
        }
        return " one of [" + toString(options) + "]";
    }

    private String toString(Collection<?> allowedNodeIds)
    {
        return StringUtils.join(allowedNodeIds.toArray(), ", ");
    }

    private boolean instanceOf(Object value, CObject type)
    {
        return instanceOf(value, type.getRmTypeName());
    }
}
