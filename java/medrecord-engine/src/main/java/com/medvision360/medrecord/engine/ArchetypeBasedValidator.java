package com.medvision360.medrecord.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.medvision360.medrecord.rmutil.AOMUtil;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.spi.ValidationReport;
import com.medvision360.medrecord.spi.base.BaseValidationReport;
import com.medvision360.medrecord.spi.base.BaseValidationResult;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.openehr.am.archetype.Archetype;
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
import org.openehr.rm.support.identification.ArchetypeID;
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
    public void check(Locatable locatable) throws ValidationException, NotSupportedException
    {
        checkNotNull(locatable, "locatable cannot be null");
        ValidationReport validationReport = validate(locatable);
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
    public ValidationReport validate(Locatable locatable) throws NotSupportedException
    {
        checkNotNull(locatable, "locatable cannot be null");
        checkSupport(locatable);

        BaseValidationReport validationReport = new BaseValidationReport();

        Archetyped archetypeDetails = locatable.getArchetypeDetails();
        ArchetypeID archetypeID = archetypeDetails.getArchetypeId();

        Archetype archetype;
        try
        {
            archetype = m_archetypeStore.get(archetypeID);
        }
        catch (NotFoundException | IOException e)
        {
            BaseValidationResult result = validationReport.newResult("/");
            result.setMessage(String.format("Cannot find archetype %s: %s", archetypeID.getValue(), e.getMessage()));
            result.setValid(false);
            result.setDetails(e);
            return validationReport;
        }
        CComplexObject complexObject = archetype.getDefinition();
        validateComplexObject(validationReport, archetype, locatable, complexObject);

        return validationReport;
    }

    private void validateComplexObject(BaseValidationReport validationReport, Archetype archetype, Locatable locatable,
            CComplexObject complexObject)
    {
        String rmEntity = complexObject.getRmTypeName();
        String path = complexObject.path();
        validateRmEntity(validationReport, locatable, rmEntity, path);

        String nodeId = complexObject.getNodeId();
        validateNodeId(validationReport, locatable, nodeId, path);

        List<CAttribute> attributes = complexObject.getAttributes();
        for (CAttribute attribute : attributes)
        {
            validateAttribute(validationReport, archetype, locatable, attribute);
        }
    }

    private void validateAttribute(BaseValidationReport validationReport, Archetype archetype, Locatable locatable,
            CAttribute attribute)
    {
        UIDBasedID uid = locatable.getUid();
        String archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();

        String attributeName = attribute.getRmAttributeName();
        String path = attribute.path();
        CAttribute.Existence existence = attribute.getExistence();

        Object value;
        try
        {
            value = get(locatable, attributeName);
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            BaseValidationResult result = validationReport.newResult(path);
            result.setMessage(String.format("Cannot retrieve value at path %s for %s of archetype %s: %s",
                    path, uid, archetypeId, e.getMessage()));
            result.setValid(false);
            result.setDetails(e);
            return;
        }
        switch (existence)
        {
            case NOT_ALLOWED:
                if (nullOrEmpty(value))
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setMessage(
                            String.format("Value at path %s not allowed for %s of archetype %s, and was not set",
                                    attribute.path(), uid, archetypeId));
                }
                return;
            case OPTIONAL:
                if (nullOrEmpty(value))
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setMessage(
                            String.format("Value at path %s optional for %s of archetype %s, and was not set",
                                    attribute.path(), uid, archetypeId));
                    return;
                }
                break;
            case REQUIRED:
            default:
                if (value == null)
                {
                    BaseValidationResult result = validationReport.newResult(path);
                    result.setValid(false);
                    result.setMessage(
                            String.format("Value at path %s required for %s of archetype %s, but was null",
                                    attribute.path(), uid, archetypeId));
                    return;
                }
                break;
        }

        validateChildren(validationReport, archetype, locatable, attribute, value);
    }

    private void validateChildren(BaseValidationReport validationReport, Archetype archetype, Locatable locatable, 
            CAttribute attribute,
            Object value)
    {
        // value != null
        UIDBasedID uid = locatable.getUid();
        String archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();

        String path = attribute.path();
        List<CObject> children = attribute.getChildren();
        if (children.size() == 0)
        {
            return;
        }
        if (attribute instanceof CSingleAttribute)
        {
            CSingleAttribute singleAttribute = (CSingleAttribute) attribute;
            List<CObject> alternatives = singleAttribute.alternatives();

            CObject alternative = validateNodeId(validationReport, uid, archetypeId, path, value, alternatives);
            if (alternative != null)
            {
                boolean validRmEntity = validateRmEntity(validationReport, uid, archetypeId, path, value, alternative);
                if (validRmEntity)
                {
                    validateObject(validationReport, archetype, alternative, value);
                }
            }
        }
        else if (attribute instanceof CMultipleAttribute)
        {
            // todo validate children
        }
        else
        {
            BaseValidationResult result = validationReport.newResult(path);
            result.setMessage(String.format("Cannot process attribute at path %s for archetype %s: unrecognized " +
                    "attribute type %s",
                    path, archetypeId, attribute.getClass().getSimpleName()));
            result.setValid(false);
            return;
        }
    }

    private CObject validateNodeId(BaseValidationReport validationReport, UIDBasedID uid, String archetypeId,
            String path, Object value, List<CObject> alternatives)
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
            allowedNodeIds.add(nodeId);
            if (locatableNodeId == null)
            {
                continue;
            }
            if (locatableNodeId.equals(nodeId))
            {
                matchedAlternative = alternative;
            }
        }

        BaseValidationResult result = validationReport.newResult(path);
        if (matchedAlternative != null)
        {
            result.setMessage(
                    String.format(
                            "Value at path %s has nodeId %s which is allowed for %s of archetype %s",
                            path, locatableNodeId, uid, archetypeId));
        }
        else
        {
            result.setValid(false);
            result.setMessage(
                    String.format(
                            "Value at path %s has nodeId %s but should be one of %s for %s of archetype %s",
                            path, locatableNodeId, StringUtils.join(allowedNodeIds.toArray()), uid, archetypeId));
        }
        return matchedAlternative;
    }

    private void validateNodeId(BaseValidationReport validationReport, Locatable locatable, String nodeId, String path)
    {
        if (nodeId == null)
        {
            return;
        }
        BaseValidationResult result = validationReport.newResult(path);
        UIDBasedID uid = locatable.getUid();
        String archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();

        String locatableNodeId = locatable.getArchetypeNodeId();
        if (nodeId.equals(locatableNodeId))
        {
            result.setMessage(String.format("at path %s nodeId is %s which is allowed for %s of archetype %s",
                    path, nodeId, uid, archetypeId));
        }
        else
        {
            result.setValid(false);
            result.setMessage(String.format("at path %s nodeId is %s but should be %s for %s of archetype %s",
                    path, locatableNodeId, nodeId, uid, archetypeId));
        }
    }

    private boolean validateRmEntity(BaseValidationReport validationReport, UIDBasedID uid, String archetypeId,
            String path, Object value, CObject alternative)
    {
        BaseValidationResult result = validationReport.newResult(path);
        String rmEntity = alternative.getRmTypeName();
        if (instanceOf(value, rmEntity))
        {
            result.setMessage(String.format(
                    "value at path %s with nodeId %s has type %s which is allowed for %s of archetype %s",
                    path, alternative.getNodeId(), value.getClass().getSimpleName(), uid, archetypeId));
            return true;
        }
        else
        {
            result.setValid(false);
            result.setMessage(String.format(
                    "value at path %s with nodeId %s has type %s but should be %s for %s of archetype %s",
                    path, alternative.getNodeId(), value.getClass().getSimpleName(), rmEntity, uid, 
                    archetypeId));
            return false;
        }
    }

    private void validateRmEntity(BaseValidationReport validationReport, Locatable value, String rmEntity,
            String path)
    {
        BaseValidationResult result = validationReport.newResult(path);
        UIDBasedID uid = value.getUid();
        String archetypeId = value.getArchetypeDetails().getArchetypeId().getValue();

        if (instanceOf(value, rmEntity))
        {
            result.setValid(false);
            result.setMessage(
                    String.format(
                            "locatable has type %s which is allowed for %s of archetype %s",
                            value.getClass().getSimpleName(), uid, archetypeId));
        }
        else
        {
            result.setValid(false);
            result.setMessage(
                    String.format(
                            "locatable has type %s but should be %s for %s of archetype %s",
                            path, value.getClass().getSimpleName(), rmEntity, uid, archetypeId));
        }
    }

    private void validateObject(BaseValidationReport validationReport, Archetype archetype, CObject constraint, 
            Object value)
    {
        // nodeId && rmEntity match, constraint != null, value != null
        String archetypeId = archetype.getArchetypeId().getValue();
        String path = constraint.path();
        String nodeId = constraint.getNodeId();
        String rmEntity = name(constraint);

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
            CDomainType domainType = (CDomainType) constraint;
        }
        else if (constraint instanceof CComplexObject)
        {
            CComplexObject complexObject = (CComplexObject) constraint;
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
            throw new NotSupportedException("Locatable not supported");
        }
    }
}
