package com.medvision360.medrecord.itest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.assertion.Assertion;
import org.openehr.am.archetype.assertion.ExpressionBinaryOperator;
import org.openehr.am.archetype.assertion.ExpressionItem;
import org.openehr.am.archetype.assertion.ExpressionLeaf;
import org.openehr.am.archetype.assertion.OperatorKind;
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
import org.openehr.am.archetype.constraintmodel.primitive.CBoolean;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CPrimitive;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.am.archetype.ontology.ArchetypeOntology;
import org.openehr.am.archetype.ontology.OntologyBinding;
import org.openehr.am.archetype.ontology.OntologyBindingItem;
import org.openehr.am.archetype.ontology.TermBindingItem;
import org.openehr.am.openehrprofile.datatypes.basic.CDvState;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantityItem;
import org.openehr.am.openehrprofile.datatypes.quantity.Ordinal;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Link;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.generic.PartyProxy;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datatypes.basic.DvState;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.ProportionKind;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvEHRURI;
import org.openehr.rm.datatypes.uri.DvURI;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.LocatableRef;
import org.openehr.rm.support.identification.ObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.UUID;
import org.openehr.rm.support.identification.VersionTreeID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;

@SuppressWarnings("UnusedDeclaration")
public class LocatableGenerator extends Terminology {
    //
    // Set up
    //
    private final static Log log = LogFactory.getLog(LocatableGenerator.class);

    private ArchetypeStore m_archetypeStore;
    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;
    private RMObjectBuilder m_rmObjectBuilder;
    private Random random = new Random();
    private String m_rmVersion = "1.0.2";
    private int m_generatedID = 0;
    private boolean m_fillOptional = true;
    private Map<UIDBasedID, Locatable> m_generated = new WeakHashMap<>();
    private Set<UIDBasedID> m_generatedUIDs = m_generated.keySet();
    private Collection<Locatable> m_generatedLocatables = m_generated.values();

    public LocatableGenerator(ArchetypeStore archetypeStore,
            TerminologyService terminologyService, MeasurementService measurementService,
            RMObjectBuilder rmObjectBuilder)
    {
        m_archetypeStore = archetypeStore;
        m_terminologyService = terminologyService;
        m_measurementService = measurementService;
        m_rmObjectBuilder = rmObjectBuilder;
    }

    public void setRmVersion(String rmVersion)
    {
        m_rmVersion = rmVersion;
    }

    //
    // API
    //
    
    public Locatable generate(String archetypeName)
            throws NotFoundException, IOException, RMObjectBuildingException, GenerateException
    {
        ArchetypeID archetypeID = new ArchetypeID(archetypeName);
        return generate(archetypeID);
    }
    
    public Locatable generate(ArchetypeID archetypeID)
            throws NotFoundException, IOException, RMObjectBuildingException, GenerateException
    {
        Archetype archetype = m_archetypeStore.get(archetypeID);
        return generate(archetype);
    }
    
    public Locatable generate(Archetype archetype)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        Object result = generateObject(archetype);
        if (!(result instanceof Locatable))
        {
            throw new RMObjectBuildingException(String.format("Constructed a %s which is not a Locatable",
                    result.getClass().getSimpleName()));
        }
        return (Locatable) result;
    }

    //
    // Object walking and structure generation
    //
    
    private Object generateObject(Archetype archetype)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        ArchetypeID archetypeID = archetype.getArchetypeId();
        String archetypeName = archetypeID.getValue();
        String className = findClassName(archetypeID);
        log.debug(String.format("Attempting to construct a %s to match %s", className, archetypeName));
        
        Map<String, Object> map = new HashMap<>();
        Archetyped archetypeDetails = new Archetyped(archetypeID, null, m_rmVersion);
        // uid added during transformation
        map.put("name", newName(className));
        map.put("archetypeDetails", archetypeDetails);
        map.put("archetypeNodeId", archetypeName);
        map.put("links", makeLinks());
        
        CComplexObject definition = archetype.getDefinition();
        generateMap(archetype, map, definition);

        return m_rmObjectBuilder.construct(className, map);
    }

    private void generateMap(Archetype archetype, Map<String, Object> map, CComplexObject definition)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        String rmTypeName = definition.getRmTypeName();
        String path = definition.path();
        String nodeId = definition.getNodeId();
        if (nodeId != null)
        {
            map.put("archetypeNodeId", nodeId);
        }
        else if (!map.containsKey("archetypeNodeId"))
        {
            map.put("archetypeNodeId", newNodeId());
        }
        nodeId = String.valueOf(map.get("archetypeNodeId"));
        //log.debug(String.format("generating map for node %s", nodeId));
        
        List<CAttribute> attributes = definition.getAttributes();
        if (attributes != null)
        {
            for (CAttribute attribute : attributes)
            {
                String attributePath = attribute.path();
                String attributeName = attribute.getRmAttributeName();
                
                if (!attribute.isAllowed())
                {
                    log.debug(String.format("Skip attribute %s, not allowed", attributePath));
                    continue;
                }
                if (!attribute.isRequired() && !m_fillOptional)
                {
                    log.debug(String.format("Skip attribute %s, not required", attributePath));
                    continue;
                }
                if (!attribute.isRequired() && !should(0.8))
                {
                    log.debug(String.format("Skip attribute %s, not required, dice said no", attributePath));
                    continue;
                }
                boolean forceMultiple = forceMultiple(rmTypeName, attributeName);
                Object value = generateAttribute(archetype, attribute, map, forceMultiple);
                log.debug(String.format("Generated attribute %s, %s: %s", attribute.path(), 
                        attribute.getRmAttributeName(), value));
                if (value != null)
                {
                    map.put(attribute.getRmAttributeName(), value);
                }
            }
        }
        
        tweakMap(archetype, map, definition);
    }

    private Object generateAttribute(Archetype archetype, CAttribute attribute, Map<String, Object> map,
            boolean forceMultiple)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        List<CObject> children = attribute.getChildren();
        if (children.size() == 0)
        {
            return null;
        }
        if (attribute instanceof CSingleAttribute)
        {
            if (!forceMultiple)
            {
                CObject choice = pick(children);
                String choiceName = name(choice);
                Object childValue = generateObject(archetype, choice, choice.isRequired(), map);
                return childValue;
            }
            else
            {
                return generateMultiple(archetype, attribute, map);
            }
        }
        else if (attribute instanceof CMultipleAttribute)
        {
            return generateMultiple(archetype, attribute, map);
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized attribute type %s",
                    attribute.getClass().getSimpleName()));
        }
    }

    private Object generateMultiple(Archetype archetype, CAttribute attribute, Map<String, Object> map)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        log.debug(String.format("generateMultiple %s %s", name(attribute), attribute.path()));
        List<CObject> children = attribute.getChildren();
        Collection<Object> container;
        boolean isUnique = false;
        if (attribute instanceof CMultipleAttribute)
        {
            isUnique = ((CMultipleAttribute)attribute).getCardinality().isUnique();
        }
        if(isUnique)
        {
            container = new TreeSet<>(); // also sorted
        }
        else
        {
            container = new ArrayList<>();
        }
        for (CObject child : children)
        {
            if (!child.isAllowed())
            {
                log.debug(String.format("Skip child %s, not allowed", child.path()));
                continue;
            }
            if (!child.isRequired() && !m_fillOptional)
            {
                log.debug(String.format("Skip child %s, not required", attribute.path()));
                continue;
            }
            Interval<Integer> occurrences = child.getOccurrences();
            int lower = occurrences.getLower() == null ? 0 :
                    Math.max(occurrences.getLower(), 0);
            int upper = occurrences.getUpper() == null ? lower + listSize(2) :
                    Math.max(occurrences.getUpper(), lower);

            int size = chooseOccurrences(isUnique, lower, upper);
            for (int i = 0; i < size; i++)
            {
                Object childValue = generateObject(archetype, child, child.isRequired(), map);
                log.debug(String.format("Generated child %s, %s: %s", child.path(),
                        attribute.getRmAttributeName(), childValue));
                container.add(childValue);
            }
        }
        return container;
    }

    private Object generateObject(Archetype archetype, ArchetypeConstraint object, boolean required,
            Map<String, Object> map)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        if (object == null)
        {
            if (required)
            {
                throw new GenerateException("null constraint");
            }
            else
            {
                log.warn("null constraint");
                return null;
            }
        }
        String objectName = name(object);
        String objectPath = object.path();
        log.debug(String.format("generateObject %s %s", objectName, objectPath));
        if (object instanceof ConstraintRef)
        {
            ConstraintRef ref = (ConstraintRef) object;
            Object value = translateConstraintRef(archetype, ref);
            return value;
        }
        else if (object instanceof ArchetypeInternalRef)
        {
            Object value = followArchetypeInternalRef(archetype, (ArchetypeInternalRef) object, required, map);
            return value;
        }
        else if (object instanceof ArchetypeSlot)
        {
            Object value = generateObjectForSlot(archetype, (ArchetypeSlot) object, required);
            return value;
        }
        else if (object instanceof CDomainType)
        {
            Object value = generateDomainType(archetype, (CDomainType) object);
            return value;
        }
        else if (object instanceof CComplexObject)
        {
            Object value = generateComplexObject(archetype, (CComplexObject) object, required);
            return value;
        }
        else if (object instanceof CPrimitiveObject)
        {
            Object value = generatePrimitive(archetype, (CPrimitiveObject)object);
            return value;
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized constraint type %s (%s) at %s", 
                    object.getClass().getSimpleName(), objectName, objectPath));
        }
    }

    private Object followArchetypeInternalRef(Archetype archetype, ArchetypeInternalRef ref, boolean required,
            Map<String, Object> map)
            throws GenerateException, RMObjectBuildingException, IOException, NotFoundException
    {
        String path = ref.getTargetPath();
        ArchetypeConstraint constraint = archetype.node(path);
        if (constraint == null)
        {
            if (required)
            {
                throw new GenerateException(String.format("Cannot resolve archetype reference %s", path));
            } else
            {
                log.warn(String.format(
                        "Skipping archetype ref %s since it is optional and we cannot resolve",
                        ref.path()));
                return null;
            }
        }
        log.debug(String.format("Following ref %s to %s", ref.path(), path));
        return generateObject(archetype, constraint, required, map);
    }

    private Object translateConstraintRef(Archetype archetype, ConstraintRef ref)
    {
        String reference = ref.getReference();
        ArchetypeOntology ontology = archetype.getOntology();
        String primaryLanguage = ontology.getPrimaryLanguage();
        //ArchetypeTerm term = ontology.constraintDefinition(primaryLanguage, reference);
        List<OntologyBinding> termBindings = ontology.getTermBindingList();
        for (OntologyBinding termBinding : termBindings)
        {
            List<OntologyBindingItem> bindingItemList = termBinding.getBindingList();
            for (OntologyBindingItem ontologyBindingItem : bindingItemList)
            {
                if (!(ontologyBindingItem instanceof TermBindingItem))
                {
                    continue;
                }
                TermBindingItem termBindingItem = (TermBindingItem) ontologyBindingItem;
                String termCode = termBindingItem.getCode();
                if (termCode.equals(reference))
                {
                    List<String> terms = termBindingItem.getTerms();
                    for (String term : terms)
                    {
                        term = term.trim();
                        if (term.startsWith("[") || term.startsWith("<"))
                        {
                            term = term.substring(1);
                        }
                        if (term.endsWith("]") || term.endsWith(">"))
                        {
                            term = term.substring(0, term.length()-1);
                        }
                        CodePhrase phrase;
                        try
                        {
                            phrase = (CodePhrase) CodePhrase.parseValue(term);
                            log.debug(String.format("Mapped ref %s to %s", reference, phrase));
                            return phrase;
                        } catch(IllegalArgumentException|ClassCastException e)
                        {
                            log.warn(String.format("Could not parse term binding %s, skipping it", term));
                            continue;
                        }
                    }
                }
            }
        }
        log.debug(String.format("Returning ref %s, no term binding done", reference));
        return reference;
    }

    private Object generateObjectForSlot(Archetype archetype, ArchetypeSlot slot, boolean required)
            throws IOException, GenerateException, NotFoundException, RMObjectBuildingException
    {
        Archetype slotArchetype = chooseArchetype(slot);
        if (slotArchetype != null)
        {
            log.debug(String.format("Satisfy slot %s with archetype %s", slot.path(), 
                    slotArchetype.getArchetypeId().getValue()));
            Object value = generateObject(archetype);
            return value;
        }
        else if (required)
        {
            throw new GenerateException(String.format("No archetypes found to match the slot at %s, " +
                    "but a value is required", slot.path()));
        }
        else
        {
            log.warn(String.format("No archetypes found to match the slot at %s, skipping it", slot.path()));
            return null;
        }
    }

    private Object generateComplexObject(Archetype archetype, CComplexObject object, boolean required)
            throws IOException, GenerateException, NotFoundException, RMObjectBuildingException
    {
        String rmType = object.getRmTypeName();
        String objectPath = object.path();
        String className = findClassName(rmType);
        Map<String, Object> map = new HashMap<>();
        log.debug(String.format("Generate complex object entity %s at %s with class %s", 
                rmType, objectPath, className));
        try
        {
            generateMap(archetype, map, object);
            return m_rmObjectBuilder.construct(className, map);
        }
        catch (IOException|GenerateException|NotFoundException|RMObjectBuildingException e)
        {
            if (!required)
            {
                log.warn(String.format(
                        "Skipping complex object at %s since it is optional and construction failed: %s",
                        object.path(), e.getMessage()), e);
                return null;
            }
            else
            {
                throw e;
            }
        }
    }

    private Object generateDomainType(Archetype archetype, CDomainType domainType) throws GenerateException
    {
        if (domainType instanceof CCodePhrase)
        {
            return generateCodePhrase(archetype, (CCodePhrase)domainType);
        }
        else if (domainType instanceof CDvQuantity)
        {
            return generateDvQuantity(archetype, (CDvQuantity) domainType);
        }
        else if (domainType instanceof CDvState)
        {
            return generateDvState(archetype, (CDvState) domainType);
        }
        else if (domainType instanceof CDvOrdinal)
        {
            return generateDvOrdinal(archetype, (CDvOrdinal) domainType);
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized domain type %s at %s",
                    domainType.getClass().getSimpleName(), domainType.path()));
        }
    }

    private Object generatePrimitive(Archetype archetype, CPrimitiveObject primitiveObject) throws GenerateException
    {
        CPrimitive primitive = primitiveObject.getItem();
        
        if (primitive instanceof CDate)
        {
            return generateDate(archetype, (CDate)primitive).toString();
        }
        else if (primitive instanceof CDateTime)
        {
            return generateDateTime(archetype, (CDateTime)primitive).toString();
            
        }
        else if (primitive instanceof CTime)
        {
            return generateTime(archetype, (CTime)primitive).toString();
        }
        else if (primitive instanceof CDuration)
        {
            return generateDuration(archetype, (CDuration)primitive).toString();
        }
        else if (primitive instanceof CBoolean)
        {
            return generateBoolean(archetype, (CBoolean)primitive);
        }
        else if (primitive instanceof CInteger)
        {
            return generateInteger(archetype, (CInteger)primitive);
        }
        else if (primitive instanceof CReal)
        {
            return generateReal(archetype, (CReal)primitive);
        }
        else if (primitive instanceof CString)
        {
            return generateString(archetype, (CString)primitive);
        }
        else if(primitive.hasAssumedValue())
        {
            return primitive.assumedValue();
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized primitive type %s at %s",
                    primitive.getClass().getSimpleName(), primitiveObject.path()));
            
        }
    }

    //
    // Archetype slot magic
    //

    private Archetype chooseArchetype(ArchetypeSlot slot) throws IOException, GenerateException, NotFoundException
    {
        String rmType = slot.getRmTypeName();
        String nodeId = slot.getNodeId();
        
        Set<Assertion> includes = slot.getIncludes();
        Set<Assertion> excludes = slot.getIncludes();
        
        Iterable<ArchetypeID> archetypes = m_archetypeStore.list();
        Set<ArchetypeID> possibilities = new HashSet<>();
        OUTER:
        for (ArchetypeID archetypeID : archetypes)
        {
            String optionRmType = archetypeID.rmEntity();
            if (!optionRmType.equalsIgnoreCase(rmType))
            {
                continue;
            }
            
            for (Assertion include : includes)
            {
                if(!testArchetypeAssertion(archetypeID, include))
                {
                    continue OUTER;
                };
            }

            for (Assertion exclude : excludes)
            {
                if(testArchetypeAssertion(archetypeID, exclude))
                {
                    continue OUTER;
                };
            }
            possibilities.add(archetypeID);
        }
        
        if (possibilities.isEmpty())
        {
            return null;
        }
        
        ArchetypeID choice = pick(possibilities);
        return m_archetypeStore.get(choice);
    }

    private boolean testArchetypeAssertion(ArchetypeID archetypeID, Assertion include) throws GenerateException
    {
        boolean match = false;
        String leftRule;
        OperatorKind op;
        String rightRule;
        String testValue;

        ExpressionItem expression = include.getExpression();
        if (!(expression instanceof ExpressionBinaryOperator))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but was %s",
                    expression.toString(),
                    expression.getClass().getSimpleName()));
        }
        ExpressionBinaryOperator binaryExpression = (ExpressionBinaryOperator) expression;

        ExpressionItem left = binaryExpression.getLeftOperand();
        if (!(left instanceof ExpressionLeaf))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but left hand side is of class %s, not ExpressionLeaf",
                    expression.toString(),
                    left.getClass().getSimpleName()));
        }

        op = binaryExpression.getOperator();
        if (!op.equals(OperatorKind.OP_EQ) && !op.equals(OperatorKind.OP_MATCHES))
        {
            throw new GenerateException(String.format(
                    "Expected string expression (x op y) for archetype slot expression \"%s\", " +
                            "but operator is of type %s, not = or matches",
                    expression.toString(),
                    op.toString()));
        }

        ExpressionItem right = binaryExpression.getRightOperand();
        if (!(right instanceof ExpressionLeaf))
        {
            throw new GenerateException(String.format(
                    "Expected binary expression (x matches y) for archetype slot expression \"%s\", " +
                            "but right hand side is of class %s, not ExpressionLeaf",
                    expression.toString(),
                    right.getClass().getSimpleName()));
        }

        ExpressionLeaf leftLeaf = (ExpressionLeaf)left;
        String leftType = leftLeaf.getType();
        if (ExpressionItem.STRING.equalsIgnoreCase(leftType) || "C_STRING".equalsIgnoreCase(leftType))
        {
            Object value = leftLeaf.getItem();
            if (value instanceof CString)
            {
                leftRule = ((CString)value).getPattern();
            }
            else
            {
                leftRule = String.valueOf(leftLeaf.getItem());
            }
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but left hand side is of type %s, not string",
                    expression.toString(),
                    leftType));
        }

        ExpressionLeaf rightLeaf = (ExpressionLeaf)right;
        String rightType = rightLeaf.getType();
        if (ExpressionItem.STRING.equalsIgnoreCase(rightType) || "C_STRING".equalsIgnoreCase(rightType))
        {
            Object value = rightLeaf.getItem();
            if (value instanceof CString)
            {
                rightRule = ((CString)value).getPattern();
            }
            else
            {
                rightRule = String.valueOf(rightLeaf.getItem());
            }
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but right hand side is of type %s, not string",
                    expression.toString(),
                    rightType));
        }

        if (leftRule.startsWith("/"))
        {
            leftRule = leftRule.substring(1);
        }
        if (leftRule.startsWith("archetype_id"))
        {
            leftRule = leftRule.substring("archetype_id".length());
        }
        if (leftRule.startsWith("/"))
        {
            leftRule = leftRule.substring(1);
        }

        if ("rm_originator".equals(leftRule))
        {
            testValue = archetypeID.rmOriginator();
        }
        else if ("rm_name".equals(leftRule))
        {
            testValue = archetypeID.rmName();
        }
        else if ("rm_entity".equals(leftRule) || "rm_type".equals(leftRule))
        {
            testValue = archetypeID.rmEntity();
        }
        else if ("concept_name".equals(leftRule))
        {
            testValue = archetypeID.conceptName();
        }
        else if ("domain_concept".equals(leftRule))
        {
            testValue = archetypeID.domainConcept();
        }
        else if ("value".equals(leftRule) || "".equals(leftRule))
        {
            testValue = archetypeID.getValue();
        }
        else
        {
            throw new GenerateException(String.format(
                    "Expected attribute expression (x matches y) for archetype slot expression \"%s\", " +
                            "but do not recognize left hand side \"%s\"",
                    expression.toString(),
                    leftLeaf.getItem()));
        }

        if (op.equals(OperatorKind.OP_EQ))
        {
            if (rightRule.equals(testValue))
            {
                match = true;
            }
        }
        else if (op.equals(OperatorKind.OP_MATCHES))
        {
            if (testValue.matches(rightRule))
            {
                match = true;
            }
        }
        
        return match;
    }

    //
    // Value generation
    //
    
    private CodePhrase generateCodePhrase(Archetype archetype, CCodePhrase domainType)
    {
        TerminologyID terminology;
        if (domainType != null)
        {
            terminology = domainType.getTerminologyId();

            if (COMPRESSION.equals(terminology))
            {
                return chooseCompression();
            }
            else if (CHECKSUMS.equals(terminology))
            {
                return chooseChecksum();
            }
            else if (STATUSES.equals(terminology))
            {
                return chooseStatus();
            }
            else if (COUNTRIES.equals(terminology))
            {
                return chooseCountry();
            }
            else if (CHARSETS.equals(terminology))
            {
                return chooseCharset();
            }
            else if (MEDIA_TYPES.equals(terminology))
            {
                return chooseMediaType();
            }
            else if (LANGUAGES.equals(terminology))
            {
                return chooseLanguage();
            }
            
            List<String> codeList = domainType.getCodeList();
            if (codeList != null && !codeList.isEmpty())
            {
                String code = pick(codeList);
                return new CodePhrase(terminology, code);
            }
            
            if (domainType.hasAssumedValue())
            {
                return domainType.getAssumedValue();
            }
            if (domainType.getDefaultValue() != null)
            {
                return domainType.getDefaultValue();
            }
            
            if (terminology.equals(OPENEHR))
            {
                // sorted by least likely to be a bad guess
                String path = domainType.path();
                if (path.contains("category"))
                {
                    return chooseCategory();
                }
                else if (path.contains("setting"))
                {
                    return chooseSetting();
                }
                else if (path.contains("subject") && path.contains("relationship"))
                {
                    return chooseSubjectRelationship();
                }
                else if (path.contains("instruction") && path.contains("transition"))
                {
                    return chooseInstructionTransition();
                }
                else if (path.contains("instruction") && path.contains("state"))
                {
                    return chooseInstructionState();
                }
                else if (path.contains("participation") && path.contains("function"))
                {
                    return chooseParticipationFunction();
                }
                else if (path.contains("participation") && path.contains("mode"))
                {
                    return chooseParticipationMode();
                }
                else if (path.contains("version"))
                {
                    return chooseVersionLifecycleState();
                }
                else if (path.contains("flavour") || path.contains("flavor"))
                {
                    return chooseNullFlavour();
                }
                else if (path.contains("math"))
                {
                    return chooseMathFunction();
                }
                else if (path.contains("multimedia"))
                {
                    return chooseMultimedia();
                }
                else if (path.contains("attest"))
                {
                    return chooseAttestation();
                }
                else if (path.contains("audit"))
                {
                    return chooseAudit();
                }
                else if (path.contains("term"))
                {
                    return chooseTermMappingPurpose();
                }
            }
        }
        else
        {
            terminology = OPENEHR;
        }
        
        TerminologyAccess terminologyAccess = m_terminologyService.terminology(terminology.name());
        if (terminologyAccess != null)
        {
            Set<CodePhrase> codes = terminologyAccess.allCodes();
            if (codes != null && !codes.isEmpty())
            {
                return pick(codes);
            }
        }
        
        return new CodePhrase(terminology, "999"+random.nextInt(10000));
    }

    private DvQuantity generateDvQuantity(Archetype archetype, CDvQuantity domainType)
    {
        String units;
        double magnitudeValue;
        int precisionValue;
        
        List<CDvQuantityItem> choices = domainType.getList();
        if (choices != null && !choices.isEmpty())
        {
            CDvQuantityItem item = pick(choices);
            Interval<Double> magnitude = item.getMagnitude();
            Interval<Integer> precision = item.getPrecision();
            units = item.getUnits();
            
            magnitudeValue = doubleFromInterval(magnitude);
            precisionValue = integerFromInterval(precision);
        }
        else
        {
            if (domainType.hasAssumedValue())
            {
                return domainType.getAssumedValue();
            }
            if (domainType.getDefaultValue() != null)
            {
                return domainType.getDefaultValue();
            }
            units = "m";
            magnitudeValue = random.nextDouble() * 50;
            precisionValue = 1+random.nextInt(4);
        }
        return new DvQuantity(units, magnitudeValue, precisionValue, m_measurementService);
    }

    private DvState generateDvState(Archetype archetype, CDvState domainType)
    {
        CodePhrase code = chooseInstructionState();
        
        DvCodedText coded = codeToText(code);
        DvState state = new DvState(coded, random.nextBoolean());
        return state;
    }

    private DvOrdinal generateDvOrdinal(Archetype archetype, CDvOrdinal domainType)
    {
        Set<Ordinal> choices = domainType.getList();
        CodePhrase symbol;
        int value;
        
        if (choices != null && !choices.isEmpty())
        {
            Ordinal choice = pick(choices);
            symbol = choice.getSymbol();
            value = choice.getValue();
        }
        else
        {
            symbol = chooseProperty();
            value = 1+random.nextInt(10);
        }

        DvCodedText codedSymbol = codeToText(symbol);
        DvOrdinal ordinal = new DvOrdinal(value, codedSymbol);
        return ordinal;
    }

    private DvDate generateDate(Archetype archetype, CDate primitive)
    {
        if (primitive != null)
        {
            List<DvDate> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvDate value = pick(choices);
                return value;
            }
        }
        
        // does not support date pattern restrictions, which are also not fully supported by the RI
        
        DvDate value = new DvDate();
        if (primitive != null)
        {
            Interval<DvDate> interval = primitive.getInterval();
            if (interval != null)
            {
                DvDate lower = interval.getLower();
                DvDate upper = interval.getUpper();
                if (lower == null && upper == null)
                {
                    return value;
                }
                if (lower != null)
                {
                    if (value.compareTo(lower) < 0) {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0) {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    private DvDateTime generateDateTime(Archetype archetype, CDateTime primitive)
    {
        if (primitive != null)
        {
            List<DvDateTime> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvDateTime value = pick(choices);
                return value;
            }
        }
        
        // does not support date pattern restrictions, which are also not fully supported by the RI
        
        DvDateTime value = new DvDateTime();
        if (primitive != null)
        {
            Interval<DvDateTime> interval = primitive.getInterval();
            if (interval != null)
            {
                DvDateTime lower = interval.getLower();
                DvDateTime upper = interval.getUpper();
                if (lower == null && upper == null)
                {
                    return value;
                }
                if (lower != null)
                {
                    if (value.compareTo(lower) < 0) {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0) {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    private DvTime generateTime(Archetype archetype, CTime primitive)
    {
        if (primitive != null)
        {
            List<DvTime> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvTime value = pick(choices);
                return value;
            }
        }
        
        // does not support date pattern restrictions, which are also not fully supported by the RI
        
        DvTime value = new DvTime();
        if (primitive != null)
        {
            Interval<DvTime> interval = primitive.getInterval();
            if (interval != null)
            {
                DvTime lower = interval.getLower();
                DvTime upper = interval.getUpper();
                if (lower == null && upper == null)
                {
                    return value;
                }
                if (lower != null)
                {
                    if (value.compareTo(lower) < 0) {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0) {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    private DvDuration generateDuration(Archetype archetype, CDuration primitive)
    {
        if (primitive != null)
        {
            Interval<DvDuration> interval = primitive.getInterval();
            if (interval != null)
            {
                DvDuration lower = interval.getLower();
                DvDuration upper = interval.getUpper();
                if (lower != null)
                {
                    return lower;
                }
                if (upper != null)
                {
                    return upper;
                }
            }
        }
        
        int hours = random.nextInt(8);
        int minutes = random.nextInt(60);
        int seconds = random.nextInt(60);
        
        DvDuration value = new DvDuration(0, 0, 0, 0, hours, minutes, seconds, 0.0);
        return value;
    }

    private String generateBoolean(Archetype archetype, CBoolean primitive)
    {
        if (primitive != null)
        {
            if (!primitive.isTrueValid())
            {
                return String.valueOf(Boolean.FALSE);
            }
            if (!primitive.isFalseValid())
            {
                return String.valueOf(Boolean.TRUE);
            }
        }
        
        return String.valueOf(random.nextBoolean());
    }

    private String generateInteger(Archetype archetype, CInteger primitive)
    {
        if (primitive != null)
        {
            Interval<Integer> interval = primitive.getInterval();
            if (interval != null)
            {
                return String.valueOf(integerFromInterval(interval));
            }
        }
        return String.valueOf(random.nextInt(10000));
    }

    private String generateReal(Archetype archetype, CReal primitive)
    {
        if (primitive != null)
        {
            Interval<Double> interval = primitive.getInterval();
            if (interval != null)
            {
                return String.valueOf(doubleFromInterval(interval));
            }
        }
        return String.valueOf(random.nextDouble() * 10000);
    }

    private String generateString(Archetype archetype, CString primitive)
    {
        if (primitive != null)
        {
            List<String> choices = primitive.getList();
            if (choices != null)
            {
                String value = pick(choices);
                return value;
            }
        }
        return newString();
    }

    private int newId()
    {
        return m_generatedID++;
    }
    
    private DvText newName(String className)
    {
        CodePhrase language = chooseLanguage();
        CodePhrase charset = chooseCharset();
        return new DvText(className + "-" + newId(), language, charset, m_terminologyService);
    }
    
    private String newNodeId()
    {
        return String.format("at999%04d", newId());
    }
    
    private String newString()
    {
        int wordCount = 1+random.nextInt(9);
        return StringGenerator.getWords(wordCount);
    }
    
    private HierObjectID makeUID()
    {
        return new HierObjectID(makeUUID(), null);
    }
    
    private UUID makeUUID()
    {
        return new UUID(java.util.UUID.randomUUID().toString());
    }
    
    protected ObjectVersionID makeOVID()
    {
        return new ObjectVersionID(makeUID().root(), new HierObjectID("medrecord.generator"), new VersionTreeID("1"));
    }

    

    private int integerFromInterval(Interval<Integer> interval)
    {
        int lower = interval.getLower() == null ? 0 :
                Math.max(interval.getLower(), 0);
        int upper = interval.getUpper() == null ? Integer.MAX_VALUE :
                Math.max(interval.getUpper(), lower);
        
        int value = random.nextInt(upper);
        if (value < lower)
        {
            value = lower;
        }
        return value;
    }

    private double doubleFromInterval(Interval<Double> interval)
    {
        double lower = interval.getLower() == null ? 0 :
                Math.max(interval.getLower(), 0);
        double upper = interval.getUpper() == null ? Double.MAX_VALUE :
                Math.max(interval.getUpper(), lower);
        
        double value = random.nextDouble() * upper;
        if (value < lower)
        {
            value = lower;
        }
        return value;
    }

    private DvCodedText codeToText(CodePhrase code)
    {
        return codeToText(code, null);
    }
    
    private DvCodedText codeToText(CodePhrase code, String terminology)
    {
        String value = null;

        TerminologyAccess terminologyAccess;
        if (terminology == null)
        {
            terminologyAccess = m_terminologyService.terminology(TerminologyService.OPENEHR);
        }
        else
        {
            terminologyAccess = m_terminologyService.terminology(terminology);
        }
        if (terminologyAccess != null)
        {
            CodePhrase languageCode = chooseLanguage();
            String languageCodeString = languageCode.getCodeString();
            String codeString = code.getCodeString();
            value = terminologyAccess.rubricForCode(codeString, languageCodeString);
        }
        
        if (value == null)
        {
            value = code.getCodeString();
        }

        return new DvCodedText(value, code);
    }

    private Set<Link> makeLinks()
    {
        if (m_generated.isEmpty() || !should(0.1))
        {
            return null;
        }
        Set<Link> links = new HashSet<>();
        for (int i = 0; i < listSize(5) && i < m_generatedUIDs.size(); i++)
        {
            UIDBasedID target = pick(m_generatedUIDs);
            DvEHRURI targetUri = new DvEHRURI("ehr://"+target.getValue());
            Link link = new Link(new DvText("I'm feeling lucky"), new DvText("generated link"), targetUri);
            links.add(link);
        }
        return links;
    }
    
    private PartyProxy makeSelf()
    {
        PartySelf partySelf = new PartySelf();
        return partySelf;
    }
    
    private PartyProxy makeSubject()
    {
        return makeSelf();
    }

    private PartyProxy makeComposer()
    {
        return makeSelf();
    }
    
    private ItemList makeDescription()
    {
        ItemList description = new ItemList(newNodeId(), newName("itemtree"), null);
        return description;
    }

    //
    // Code selection
    //
    
    private CodePhrase chooseCategory()
    {
        return choose(CATEGORIES);
    }

    private CodePhrase chooseAttestation()
    {
        return choose(ATTEST);
    }

    private CodePhrase chooseAudit()
    {
        return choose(AUDIT);
    }

    private CodePhrase chooseMultimedia()
    {
        return choose(MULTIMEDIA);
    }

    private CodePhrase chooseProperty()
    {
        return choose(PROPERTY);
    }

    private CodePhrase chooseVersionLifecycleState()
    {
        return choose(VERSION_LIFECYCLE);
    }

    private CodePhrase chooseParticipationFunction()
    {
        return choose(PARTICIPATION_FUNCTION);
    }

    private CodePhrase chooseNullFlavour()
    {
        return choose(NULL_FLAVOUR);
    }

    private CodePhrase chooseParticipationMode()
    {
        return choose(PARTICIPATION_MODE);
    }

    private CodePhrase chooseInstructionState()
    {
        return choose(INSTRUCTION_STATE);
    }

    private CodePhrase chooseInstructionTransition()
    {
        return choose(INSTRUCTION_TRANSITION);
    }

    private CodePhrase chooseSubjectRelationship()
    {
        return choose(SUBJECT_RELATIONSHIP);
    }

    private CodePhrase chooseTermMappingPurpose()
    {
        return choose(TERM_MAPPING_PURPOSE);
    }

    private CodePhrase chooseMathFunction()
    {
        return choose(MATH_FUNCTION);
    }

    private CodePhrase chooseSetting()
    {
        return choose(SETTING);
    }

    private CodePhrase chooseCompression()
    {
        return choose(COMPRESSIONS);
    }

    private CodePhrase chooseChecksum()
    {
        return choose(CHECKSUMS);
    }

    private CodePhrase chooseStatus()
    {
        return choose(STATUSES);
    }

    private CodePhrase chooseCountry()
    {
        return choose(COUNTRIES);
    }

    private CodePhrase chooseCharset()
    {
        return choose(CHARSETS);
    }

    private CodePhrase chooseMediaType()
    {
        return choose(MEDIA_TYPES);
    }

    private CodePhrase chooseLanguage()
    {
        return choose(LANGUAGES);
    }
    
    private CodePhrase choose(CodePhrase[] phrases)
    {
        return pick(phrases);
    }
    
    //
    // RM augmentation
    //

    /**
     * Allows overriding the decision of whether to fill one child attribute out of a list of possible choices, 
     * or instead provide a Collection instance.
     */
    private boolean forceMultiple(String rmTypeName, String attributeName)
    {
        switch (rmTypeName)
        {
            // structure
            case "ITEM_LIST":
            case "ITEM_TREE":
            case "CLUSTER":
                switch (attributeName)
                {
                    case "items":
                        return true;
                }
                break;
            case "ITEM_TABLE":
                switch (attributeName)
                {
                    case "rows":
                        return true;
                }
                break;
            case "EVENT":
            case "INTERVAL_EVENT":
            case "POINT_EVENT":
                switch (attributeName)
                {
                    case "state":
                        return true;
                }
                break;
            case "HISTORY":
                switch (attributeName)
                {
                    case "events":
                        return true;
                }
                break;
            
            // directory
            case "FOLDER":
                switch (attributeName)
                {
                    case "folders":
                    case "items":
                        return true;
                }
                break;
            
            // change control
            case "CONTRIBUTION":
                switch (attributeName)
                {
                    case "versions":
                        return true;
                }
                break;
            case "ORIGINAL_VERSION":
                switch (attributeName)
                {
                    case "otherInputVersionUids":
                    case "attestations":
                        return true;
                }
                break;
            
            // generic
            case "ATTESTATION":
                switch (attributeName)
                {
                    case "items":
                    case "attestations":
                        return true;
                }
                break;
            case "PARTY_IDENTIFIED":
            case "PARTY_RELATED":
                switch (attributeName)
                {
                    case "identifiers":
                        return true;
                }
                break;
            case "REVISION_HISTORY":
                switch (attributeName)
                {
                    case "items":
                        return true;
                }
                break;
            case "REVISION_HISTORY_ITEM":
                switch (attributeName)
                {
                    case "audits":
                        return true;
                }
                break;
            
            // todo resource package uses Map...
            
            // composition
            case "COMPOSITION":
                switch (attributeName)
                {
                    case "content":
                        return true;
                }
                break;
            case "EVENT_CONTEXT":
                switch (attributeName)
                {
                    case "participations":
                        return true;
                }
                break;
            case "SECTION":
                switch (attributeName)
                {
                    case "items":
                        return true;
                }
                break;
            case "ACTION":
            case "ACTIVITY":
            case "ADMIN_ENTRY":
            case "EVALUATION":
            case "INSTRUCTION":
            case "OBSERVATION":
                switch (attributeName)
                {
                    case "otherParticipations":
                        return true;
                }
                break;
            
            // demographic
            case "AGENT":
            case "GROUP":
            case "ORGANISATION":
            case "PERSON":
            case "ROLE":
                switch (attributeName)
                {
                    case "identities":
                    case "contacts":
                    case "relationships":
                    case "reverseRelationships":
                    case "roles":
                        return true;
                }
                break;
            case "CONTACT":
                switch (attributeName)
                {
                    case "addresses":
                        return true;
                }
                break;
        }
        return false;
    }

    private void tweakMap(Archetype archetype, Map<String, Object> map, CComplexObject definition) throws IOException
    {
        String rmTypeName = toUnderscoreSeparated(definition.getRmTypeName());
        //log.debug(String.format("Extending map for type %s", rmTypeName));
        
        boolean hasValue = hasValue(map);
        boolean hasName = hasName(map);
        if (!hasName)
        {
            addName(map, rmTypeName.toLowerCase());
        }
        
        if (!hasValue)
        {
            switch (rmTypeName)
            {
                // primitives
                case "DV_DATE":
                    addValue(map, generateDate(archetype, null).toString());
                    break;
                case "DV_DATE_TIME":
                    addValue(map, generateDateTime(archetype, null).toString());
                    break;
                case "DV_TIME":
                    addValue(map, generateTime(archetype, null).toString());
                    break;
                case "DV_DURATION":
                    addValue(map, generateDuration(archetype, null).toString());
                    break;
                case "DV_BOOLEAN":
                    addValue(map, generateBoolean(archetype, null));
                    break;
                case "DV_COUNT":  // "DV_INTEGER":
                    addValue(map, generateInteger(archetype, null));
                    if (!map.containsKey("magnitude"))
                    {
                        map.put("magnitude", "1");
                    }
                    break;
                case "DV_TEXT": // "DV_STRING":
                    addValue(map, generateString(archetype, null));
                    break;

                case "DV_URI":
                    addValue(map, "urn:generated:"+newName("uri"));
                    break;
                case "DV_EHRURI":
                case "DV_EHR_URI":
                    addValue(map, "ehr://generated/"+newName("uri"));
                    break;
            }
        }
        
        switch (rmTypeName)
        {
            // domain types                    
            case "DV_CODED_TEXT":
                CodePhrase code;
                if (map.containsKey("defining_code"))
                {
                    Object obj = map.get("defining_code");
                    if (obj instanceof CodePhrase)
                    {
                        code = (CodePhrase) obj;
                    }
                    else
                    {
                        code = generateCodePhrase(archetype, null);
                    }
                }
                else
                {
                    code = generateCodePhrase(archetype, null);
                }
                map.put("defining_code", code);
                if (!hasValue)
                    addValue(map, codeToText(code).getValue());
                break;
            
            case "DV_PROPORTION":
                if (!map.containsKey("numerator"))
                    map.put("numerator", String.valueOf(generateReal(archetype, null)));
                if (!map.containsKey("denominator"))
                    map.put("denominator", String.valueOf(generateReal(archetype, null)));
                if (!map.containsKey("precision"))
                    map.put("precision", String.valueOf(generateInteger(archetype, null)));
                if (!map.containsKey("type"))
                    map.put("type", ProportionKind.RATIO);
                break;
            
            case "DV_INTERVAL":
            case "DV_INTERVAL<DV_QUANTITY>":
            case "DV_INTERVAL<DV_REAL>":
            case "DV_INTERVAL<DV_INTEGER>":
            case "DV_INTERVAL<DV_DATE_TIME>":
                if (map.containsKey("upper") && map.containsKey("lower"))
                {
                    Object upper = map.get("upper");
                    Object lower = map.get("lower");
                    //noinspection unchecked
                    if (upper instanceof Comparable && lower instanceof Comparable)
                    {
                        //noinspection unchecked
                        Comparable upperQ = (Comparable) upper;
                        //noinspection unchecked
                        Comparable lowerQ = (Comparable) lower;
                        //noinspection unchecked
                        if (upperQ.compareTo(lowerQ) < 0)
                        {
                            map.put("upper", lower);
                            map.put("lower", upper);
                        }
                    }
                    if (upper instanceof Double && lower instanceof Double)
                    {
                        double upperD = (Double) upper;
                        double lowerD = (Double) lower;
                        if (upperD < lowerD)
                        {
                            map.put("upper", lower);
                            map.put("lower", upper);
                        }
                    }
                    if (upper instanceof Integer && lower instanceof Integer)
                    {
                        int upperI = (Integer) upper;
                        int lowerI = (Integer) lower;
                        if (upperI < lowerI)
                        {
                            map.put("upper", lower);
                            map.put("lower", upper);
                        }
                    }
                }
                break;
            
            // identification
//            case "ARCHETYPE_ID":
//                map.put("value", "openEHR-EHR-ADMIN_ENTRY.admin_entry.v1");
//                break;
            case "DV_IDENTIFIER":
                if (!map.containsKey("issuer"))
                    map.put("issuer", ""+newString());
                if (!map.containsKey("assigner"))
                    map.put("assigner", ""+newString());
                if (!map.containsKey("id"))
                    map.put("id", ""+newId());
                if (!map.containsKey("type"))
                    map.put("type", ""+newString());
                break;
            case "PARTY_REF":
                if (!map.containsKey("id"))
                    map.put("id", makeUID());
                if (!map.containsKey("type"))
                    map.put("type", ""+newString());
                break;
            case "LOCATABLE_REF":
                if (!map.containsKey("id"))
                    map.put("id", makeOVID());
                if (!map.containsKey("namespace"))
                    map.put("namespace", newName("namespace").getValue());
                if (!map.containsKey("type"))
                    map.put("type", newName("type").getValue());
                break;
            
            // composition
            case "COMPOSITION":
                if (!map.containsKey("composer"))
                    map.put("composer", makeSubject());
                if (!map.containsKey("category"))
                    map.put("category", codeToText(chooseCategory()));
                if (map.containsKey("context"))
                    map.put("category", codeToText(CATEGORY_event));
                map.remove("parent");
                break;
            case "EVENT_CONTEXT":
                if (!map.containsKey("start_time"))
                    map.put("start_time", generateDateTime(archetype, null));
                if (!map.containsKey("setting"))
                    map.put("setting", codeToText(chooseSetting()));
                map.remove("participations");
                break;
            case "ACTION":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                if (!map.containsKey("time"))
                    map.put("time", generateDateTime(archetype, null));
                break;
            case "ACTIVITY":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                if (!map.containsKey("timing"))
                    map.put("timing", new DvParsable(newString(), "txt"));
                if (!map.containsKey("action_archetype_id"))
                    map.put("action_archetype_id", newString());
                break;
            case "INSTRUCTION":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("narrative"))
                    map.put("narrative", new DvText(newString()));
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                break;
            case "ADMIN_ENTRY":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                break;
            case "EVALUATION":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                break;
            case "OBSERVATION":
                if (!map.containsKey("subject"))
                    map.put("subject", makeSubject());
                if (!map.containsKey("description") || map.get("description") == null)
                    map.put("description", makeDescription());
                break;
            case "INSTRUCTION_DETAILS":
                if (!map.containsKey("instructionId"))
                {
                    map.put("instructionId", new LocatableRef(
                            makeOVID(), newName("namespace").getValue(), newName("type").getValue(), null));
                }
                if (!map.containsKey("activityId"))
                    map.put("activityId", newName("activity").getValue());
                break;
            
            // demographic
            case "PARTICIPATION":
                if (!map.containsKey("performer"))
                    map.put("performer", makeSelf());
                // todo at-term-binding-mapping isn't working for these
                //if (!map.containsKey("mode"))
                map.put("mode", codeToText(chooseParticipationMode()));
                //if (!map.containsKey("function"))
                map.put("function", codeToText(chooseParticipationFunction()));

            // structure
            case "HISTORY":
                if (!map.containsKey("origin"))
                    map.put("origin", generateDateTime(archetype, null));
                break;

            // other
            case "EVENT":
            case "POINT_EVENT":
            case "INTERVAL_EVENT":
                if (!map.containsKey("time"))
                    map.put("time", generateDateTime(archetype, null));
                break;
            case "DV_MULTIMEDIA":
                if (!map.containsKey("mediaType"))
                    map.put("mediaType", chooseMediaType());
                if (!map.containsKey("compressionAlgorithm"))
                    map.put("compressionAlgorithm", chooseCompression());
                if (!map.containsKey("integrityCheckAlgorithm"))
                    map.put("integrityCheckAlgorithm", chooseChecksum());
                if (!map.containsKey("uri"))
                    map.put("uri", new DvURI("urn:generated:"+newName("uri")));
                
        }
        
        if (!map.containsKey("encoding"))
            map.put("encoding", chooseCharset());
        if (!map.containsKey("language"))
            map.put("language", chooseLanguage());
        if (!map.containsKey("territory"))
            map.put("territory", chooseCountry());
        
        if (map.containsKey("value") && map.get("value") != null)
        {
            map.remove("nullFlavour");
        }
        else
        {
            map.put("nullFlavour", codeToText(chooseNullFlavour()));
        }
    }

    private void addValue(Map<String, Object> map, Object value)
    {
        map.put("value", value);
    }

    private boolean hasValue(Map<String, Object> map)
    {
        return map.containsKey("value");
    }

    private void addName(Map<String, Object> map, String className)
    {
        map.put("name", newName(className));
    }

    private boolean hasName(Map<String, Object> map)
    {
        return map.containsKey("name");
    }
    //
    // Utilities
    //

    private String findClassName(ArchetypeID archetypeID)
    {
        // can map to different types here
        return findClassName(archetypeID.rmEntity());
    }

    private String findClassName(String rmType)
    {
        // can map to different types here
        rmType = toUnderscoreSeparated(rmType);
        
        switch (rmType)
        {
            case "EVENT":
                return "POINT_EVENT";
            case "ITEM_STRUCTURE":
                return "ITEM_LIST";
            default:
                return rmType;
        }
    }

    private <T> T pick(T[] options)
    {
        if (options == null)
        {
            log.warn("Nothing to pick from");
            return null;
        }
        int length = options.length;
        if (length == 0)
        {
            log.warn("Empty array to pick from");
            return null;
        }
        if (length == 1)
        {
            return options[0];
        }
        
        int pick = random.nextInt(options.length);
        return options[pick];
    }
    
    private <T> T pick(Iterable<T> options)
    {
        if (options == null)
        {
            return null;
        }
        List<T> optionList = Lists.newArrayList(options);
        if (optionList.isEmpty())
        {
            return null;
        }
        if (optionList.size() == 1)
        {
            return optionList.get(0);
        }
        
        int pick = random.nextInt(optionList.size());
        return optionList.get(pick);
    }
    
    private boolean should(double probability)
    {
        return true;
        //return random.nextDouble() < probability;
    }
    
    private int listSize(int maxSize)
    {
        maxSize = Math.max(1, maxSize);
        if (maxSize == 1)
        {
            return 1;
        }
        return 1 + random.nextInt(maxSize - 1);
    }
    
    private boolean should()
    {
        return true;
        //return random.nextBoolean();
    }
    
    private int chooseOccurrences(boolean unique, int lower, int upper)
    {
        int occurrences;
        if (upper == 0)
        {
            occurrences = 0;
        }
        else if (unique && lower <= 1 && upper >= 1)
        {
            occurrences = 1;
        }
        else
        {
            occurrences = listSize(upper);
        }
        return occurrences;
    }
    
    private String toUnderscoreSeparated(String camelCase)
    {
        if (camelCase.indexOf("_") != -1)
        {
            return camelCase.toUpperCase();
        }
        
        String[] array = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++)
        {
            String s = array[i];
            buf.append(s.toUpperCase());
            if (i != array.length - 1)
            {
                buf.append("_");
            }
        }
        return buf.toString();
    }

    private String name(ArchetypeConstraint constraint)
    {
        if (constraint == null)
        {
            return null;
        }
        if (constraint instanceof CObject)
        {
            CObject object = (CObject) constraint;
            return object.getRmTypeName();
        }
        else if (constraint instanceof CAttribute)
        {
            CAttribute attribute = (CAttribute) constraint;
            return attribute.getRmAttributeName();
        }
        return constraint.getClass().getSimpleName();
    }

}
