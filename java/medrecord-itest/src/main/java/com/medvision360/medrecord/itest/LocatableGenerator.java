package com.medvision360.medrecord.itest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;

import com.medvision360.medrecord.rmutil.AOMUtil;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.archetype.assertion.Assertion;
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
import org.openehr.am.archetype.constraintmodel.Cardinality;
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
import org.openehr.am.openehrprofile.datatypes.basic.CDvState;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Link;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvEHRURI;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.UIDBasedID;

/**
 * Programmatic generator of {@link Locatable} instances that match an {@link Archetype}.
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked", "rawtypes"})
public class LocatableGenerator extends AOMUtil
{
    private final static Log log = LogFactory.getLog(LocatableGenerator.class);

    private ValueGenerator m_valueGenerator;
    private RMAdapter m_rmAdapter;
    private AssertionSupport m_assertionSupport;
    private RandomSupport m_randomSupport;
    private ArchetypeStore m_archetypeStore;
    private String m_rmVersion = "1.0.2";
    private boolean m_fillOptional = true;
    private Map<UIDBasedID, Locatable> m_generated = new WeakHashMap<>();
    private Set<UIDBasedID> m_generatedUIDs = m_generated.keySet();
    private Collection<Locatable> m_generatedLocatables = m_generated.values();

    public LocatableGenerator(ArchetypeStore archetypeStore, RandomSupport randomSupport,
            AssertionSupport assertionSupport, ValueGenerator valueGenerator, RMAdapter rmAdapter,
            Map<SystemValue, Object> systemValues)
    {
        super(systemValues);
        m_archetypeStore = archetypeStore;
        m_randomSupport = randomSupport;
        m_assertionSupport = assertionSupport;
        m_valueGenerator = valueGenerator;
        m_rmAdapter = rmAdapter;
    }

    public void setRmVersion(String rmVersion)
    {
        m_rmVersion = rmVersion;
    }

    //
    // API
    //

    public Locatable generate(String archetypeName)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        ArchetypeID archetypeID = new ArchetypeID(archetypeName);
        return generate(archetypeID);
    }

    public Locatable generate(ArchetypeID archetypeID)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        WrappedArchetype wrappedArchetype = m_archetypeStore.get(archetypeID);
        return generate(wrappedArchetype);
    }

    public Locatable generate(WrappedArchetype wrappedArchetype)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        return generate(wrappedArchetype.getArchetype());
    }

    public Locatable generate(Archetype archetype)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        List<Archetype> parents = new ArrayList<>();
        parents.add(archetype);

        Object result = generateObject(parents);
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

    protected Object generateObject(List<Archetype> parents)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        Archetype archetype = current(parents);
        ArchetypeID archetypeID = archetype.getArchetypeId();
        String archetypeName = archetypeID.getValue();
        String className = m_rmAdapter.findConcreteClassName(archetypeID);
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Attempting to construct a %s to match %s", className, archetypeName));
        }

        Map<String, Object> map = new HashMap<>();
        Archetyped archetypeDetails = new Archetyped(archetypeID, null, m_rmVersion);
        // uid added during transformation
        map.put("name", m_valueGenerator.generateName(className));
        map.put("archetypeDetails", archetypeDetails);
        map.put("archetypeNodeId", archetypeName);
        map.put("links", generateLinks());

        CComplexObject definition = archetype.getDefinition();
        generateMap(parents, map, definition);

        return construct(className, map);
    }

    protected void generateMap(List<Archetype> parents, Map<String, Object> map, CComplexObject definition)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
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
            map.put("archetypeNodeId", m_valueGenerator.generateNodeId());
        }
        //nodeId = String.valueOf(map.get("archetypeNodeId"));
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
                if (!attribute.isRequired() && !m_randomSupport.should(0.8))
                {
                    log.debug(String.format("Skip attribute %s, not required, dice said no", attributePath));
                    continue;
                }
                boolean forceMultiple = m_rmAdapter.forceMultiple(rmTypeName, attributeName);
                Object value = generateAttribute(parents, attribute, map, forceMultiple);
                log.debug(String.format("Generated attribute %s, %s: %s", attribute.path(),
                        attribute.getRmAttributeName(), value));
                if (value != null)
                {
                    map.put(attribute.getRmAttributeName(), value);
                }
            }
        }

        m_rmAdapter.tweakValueMap(map, definition);
    }

    protected Object generateAttribute(List<Archetype> parents, CAttribute attribute, Map<String, Object> map,
            boolean forceMultiple)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        List<CObject> children = attribute.getChildren();
        if (children.size() == 0)
        {
            return null;
        }
        if (attribute instanceof CSingleAttribute)
        {
            CObject choice = m_randomSupport.pick(children);
            String choiceName = name(choice);
            Object childValue = generateObject(parents, choice, choice.isRequired(), map);

            if (forceMultiple)
            {
                List<Object> container = new ArrayList<>();
                container.add(childValue);
                return container;
            }
            else
            {
                return childValue;
            }
        }
        else if (attribute instanceof CMultipleAttribute)
        {
            return generateMultiple(parents, attribute, map);
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized attribute type %s",
                    attribute.getClass().getSimpleName()));
        }
    }

    protected Object generateMultiple(List<Archetype> parents, CAttribute attribute, Map<String, Object> map)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        String attributeName = name(attribute);
        if (log.isDebugEnabled())
        {
            log.debug(String.format("generateMultiple %s %s", attributeName, attribute.path()));
        }
        List<CObject> children = attribute.getChildren();
        Collection<Object> container = generateContainer(attribute);
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
            int upper = occurrences.getUpper() == null ? lower + m_randomSupport.listSize(2) :
                    Math.max(occurrences.getUpper(), lower);

            int size = m_randomSupport.chooseOccurrences(container instanceof Set, lower, upper);
            for (int i = 0; i < size; i++)
            {
                generateChild(parents, attribute, map, container, child);
            }
        }
        return container;
    }

    protected Collection<Object> generateContainer(CAttribute attribute)
    {
        Collection<Object> container;
        boolean isUnique = false;
        boolean isOrdered = false;
        if (attribute instanceof CMultipleAttribute)
        {
            CMultipleAttribute multipleAttribute = (CMultipleAttribute) attribute;
            Cardinality cardinality = multipleAttribute.getCardinality();
            isUnique = cardinality.isUnique();
            isOrdered = cardinality.isOrdered();
        }
        if (isUnique)
        {
            if (isOrdered)
            {
                container = new TreeSet<>();
            }
            else
            {
                container = new HashSet<>();
            }
        }
        else
        {
            container = new ArrayList<>();
        }
        return container;
    }

    private void generateChild(List<Archetype> parents, CAttribute attribute, Map<String, Object> map,
            Collection<Object> container, CObject child)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
    {
        Object childValue = generateObject(parents, child, child.isRequired(), map);
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Generated child %s, %s: %s", child.path(),
                    attribute.getRmAttributeName(), childValue));
        }
        if (childValue == null)
        {
            if (!child.isRequired())
            {
                if (log.isDebugEnabled())
                {
                    log.debug(String.format("Skip child %s, generated a null child", attribute.path()));
                }
                return;
            }
            else
            {
                throw new GenerateException(String.format("Generated a null child for %s", attribute.path()));
            }
        }
        container.add(childValue);
    }

    protected Object generateObject(List<Archetype> parents, ArchetypeConstraint object, boolean required,
            Map<String, Object> map)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException, ParseException
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
        if (log.isDebugEnabled())
        {
            log.debug(String.format("generateObject %s %s", objectName, objectPath));
        }
        if (object instanceof ConstraintRef)
        {
            ConstraintRef ref = (ConstraintRef) object;
            Object value = translateConstraintRef(current(parents), ref);
            return value;
        }
        else if (object instanceof ArchetypeInternalRef)
        {
            Object value = followArchetypeInternalRef(parents, (ArchetypeInternalRef) object, required, map);
            return value;
        }
        else if (object instanceof ArchetypeSlot)
        {
            Object value = generateObjectForSlot(parents, (ArchetypeSlot) object, required);
            return value;
        }
        else if (object instanceof CDomainType)
        {
            Object value = generateDomainType(parents, (CDomainType) object);
            return value;
        }
        else if (object instanceof CComplexObject)
        {
            Object value = generateComplexObject(parents, (CComplexObject) object, required);
            return value;
        }
        else if (object instanceof CPrimitiveObject)
        {
            Object value = generatePrimitive(parents, (CPrimitiveObject) object);
            return value;
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized constraint type %s (%s) at %s",
                    object.getClass().getSimpleName(), objectName, objectPath));
        }
    }

    protected Object followArchetypeInternalRef(List<Archetype> parents, ArchetypeInternalRef ref, boolean required,
            Map<String, Object> map)
            throws GenerateException, RMObjectBuildingException, IOException, NotFoundException, ParseException
    {
        String path = ref.getTargetPath();
        ArchetypeConstraint constraint = current(parents).node(path);
        if (constraint == null)
        {
            if (required)
            {
                throw new GenerateException(String.format("Cannot resolve archetype reference %s", path));
            }
            else
            {
                log.warn(String.format(
                        "Skipping archetype ref %s since it is optional and we cannot resolve",
                        ref.path()));
                return null;
            }
        }
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Following ref %s to %s", ref.path(), path));
        }
        return generateObject(parents, constraint, required, map);
    }

    private Archetype current(List<Archetype> parents)
    {
        return parents.get(parents.size()-1);
    }

    protected Object generateObjectForSlot(List<Archetype> parents, ArchetypeSlot slot, boolean required)
            throws IOException, GenerateException, NotFoundException, RMObjectBuildingException, ParseException
    {
        Archetype current = current(parents);
        Archetype slotArchetype = chooseArchetype(parents, slot);
        if (slotArchetype != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug(String.format("Satisfy slot %s with archetype %s", slot.path(),
                        slotArchetype.getArchetypeId().getValue()));
            }
            List<Archetype> newParents = new ArrayList<>();
            newParents.addAll(parents);
            newParents.add(slotArchetype);
            Object value = generateObject(newParents);
            return value;
        }
        else if (required)
        {
            throw new GenerateException(String.format("No archetypes found to match the slot at %s in archetype %s, " +
                    "but a value is required", slot.path(), current.getArchetypeId().getValue()));
        }
        else
        {
            log.debug(String.format("No archetypes found to match the slot at %s in archetype %s, skipping it",
                    slot.path(), current.getArchetypeId().getValue()));
            return null;
        }
    }

    protected Object generateComplexObject(List<Archetype> parents, CComplexObject object, boolean required)
            throws IOException, GenerateException, NotFoundException, RMObjectBuildingException, ParseException
    {
        String rmType = object.getRmTypeName();
        Object result = generateCustomComplexObject(rmType);
        if (result != null)
        {
            return result;
        }
        String objectPath = object.path();
        String className = m_rmAdapter.findConcreteClassName(rmType);
        Map<String, Object> map = new HashMap<>();
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Generate complex object entity %s at %s with class %s",
                    rmType, objectPath, className));
        }
        try
        {
            generateMap(parents, map, object);
            return construct(className, map);
        }
        catch (IOException | GenerateException | NotFoundException | RMObjectBuildingException | ParseException e)
        {
            if (!required)
            {
                boolean empty = false;
                String message = e.getMessage();
                if (message != null && message.contains("empty items"))
                {
                    empty = true;
                }
                else
                {
                    Throwable cause = e.getCause();
                    while (cause != null)
                    {
                        message = cause.getMessage();
                        if (message != null && message.contains("empty items"))
                        {
                            empty = true;
                        }
                        cause = cause.getCause();
                    }
                }
                if (empty)
                {
                    log.warn(String.format(
                            "Skipping complex object at %s since it is optional and construction failed: %s",
                            object.path(), e.getMessage()));
                }
                else
                {
                    log.warn(String.format(
                            "Skipping complex object at %s since it is optional and construction failed: %s",
                            object.path(), e.getMessage()), e);

                }

                return null;
            }
            else
            {
                throw e;
            }
        }
    }

    public Object generateCustomComplexObject(String rmType)
    {
        rmType = m_rmAdapter.toRmEntityName(rmType);

        switch (rmType)
        {
            case "OBJECT_ID":
                return m_valueGenerator.generateUID();
            case "UID_BASED_ID":
                return m_valueGenerator.generateUID();
            case "ARCHETYPE_ID":
                try
                {
                    Iterable<ArchetypeID> archetypeIDS = m_archetypeStore.list();
                    ArchetypeID archetypeID = m_randomSupport.pick(archetypeIDS);
                    return archetypeID;
                }
                catch (IOException e)
                {
                    return new ArchetypeID("openEHR-EHR-INSTRUCTION.generated_instruction.v1test");
                }
            default:
                return null;
        }
    }

    protected Object generateDomainType(List<Archetype> parents, CDomainType domainType) throws GenerateException
    {
        if (domainType instanceof CCodePhrase)
        {
            return m_valueGenerator.generateCodePhrase((CCodePhrase) domainType);
        }
        else if (domainType instanceof CDvQuantity)
        {
            return m_valueGenerator.generateDvQuantity((CDvQuantity) domainType);
        }
        else if (domainType instanceof CDvState)
        {
            return m_valueGenerator.generateDvState((CDvState) domainType);
        }
        else if (domainType instanceof CDvOrdinal)
        {
            return m_valueGenerator.generateDvOrdinal((CDvOrdinal) domainType);
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized domain type %s at %s",
                    domainType.getClass().getSimpleName(), domainType.path()));
        }
    }

    protected Object generatePrimitive(List<Archetype> parents, CPrimitiveObject primitiveObject) throws GenerateException
    {
        CPrimitive primitive = primitiveObject.getItem();

        if (primitive instanceof CDate)
        {
            return m_valueGenerator.generateDate((CDate) primitive).toString();
        }
        else if (primitive instanceof CDateTime)
        {
            return m_valueGenerator.generateDateTime((CDateTime) primitive).toString();

        }
        else if (primitive instanceof CTime)
        {
            return m_valueGenerator.generateTime((CTime) primitive).toString();
        }
        else if (primitive instanceof CDuration)
        {
            return m_valueGenerator.generateDuration((CDuration) primitive).toString();
        }
        else if (primitive instanceof CBoolean)
        {
            return m_valueGenerator.generateBoolean((CBoolean) primitive);
        }
        else if (primitive instanceof CInteger)
        {
            return m_valueGenerator.generateInteger((CInteger) primitive);
        }
        else if (primitive instanceof CReal)
        {
            return m_valueGenerator.generateReal((CReal) primitive);
        }
        else if (primitive instanceof CString)
        {
            return m_valueGenerator.generateString((CString) primitive);
        }
        else if (primitive.hasAssumedValue())
        {
            return primitive.assumedValue();
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized primitive type %s at %s",
                    primitive.getClass().getSimpleName(), primitiveObject.path()));

        }
    }

    protected Archetype chooseArchetype(List<Archetype> parents, ArchetypeSlot slot)
            throws IOException, GenerateException, NotFoundException, ParseException
    {
        String rmType = slot.getRmTypeName();
        String nodeId = slot.getNodeId();

        Set<Assertion> includes = slot.getIncludes();
        Set<Assertion> excludes = slot.getExcludes();

        Iterable<ArchetypeID> archetypes = m_archetypeStore.list();
        Set<ArchetypeID> possibilities = new HashSet<>();
        OUTER:
        for (ArchetypeID archetypeID : archetypes)
        {
            for (Archetype parent : parents)
            {
                if (archetypeID.equals(parent.getArchetypeId())) {
                    // prevent infinite recursion a la openEHR-EHR-CLUSTER.palpation.v1
                    // and even silly one like
                    //  openEHR-EHR-CLUSTER.symptom.v1.adl > 
                    //    openEHR-EHR-CLUSTER.symptom-pain.v1.adl > 
                    //      openEHR-EHR-CLUSTER.symptom.v1.adl >
                    //        ...
                    continue OUTER;
                }
            }
            
            String optionRmType = archetypeID.rmEntity();
            if (!optionRmType.equalsIgnoreCase(rmType))
            {
                Class<?> optionType;
                try
                {
                    optionType = retrieveRMType(optionRmType);
                }
                catch (RMObjectBuildingException e)
                {
                    log.warn(String.format("Archetype %s specifies unrecognized RM entity %s", archetypeID, 
                            optionRmType));
                    continue;
                }

                Class<?> expectedType;
                try
                {
                    expectedType = retrieveRMType(rmType);
                }
                catch (RMObjectBuildingException e)
                {
                    log.warn(String.format("Archetype %s specifies unrecognized RM entity %s", archetypeID, 
                            rmType));
                    continue;
                }
                
                if (!expectedType.isAssignableFrom(optionType))
                {
                    continue;
                }
            }

            if (includes != null)
            {
                boolean included = false;
                for (Assertion include : includes)
                {
                    if (m_assertionSupport.testArchetypeAssertion(archetypeID, include))
                    {
                        included = true;
                        break; // any include is ok
                    }
                }
                if (!included)
                {
                    continue;
                }
            }
            
            if (excludes != null)
            {
                for (Assertion exclude : excludes)
                {
                    if (m_assertionSupport.testArchetypeAssertion(archetypeID, exclude))
                    {
                        continue OUTER; // any exclude is not ok
                    }
                }
            }
            possibilities.add(archetypeID);
        }

        if (possibilities.isEmpty())
        {
            return null;
        }

        ArchetypeID choice = m_randomSupport.pick(possibilities);
        WrappedArchetype wrappedResult = m_archetypeStore.get(choice);
        return wrappedResult.getArchetype();
    }

    protected Set<Link> generateLinks()
    {
        if (m_generated.isEmpty() || !m_randomSupport.should(0.1))
        {
            return null;
        }
        Set<Link> links = new HashSet<>();
        for (int i = 0; i < m_randomSupport.listSize(5) && i < m_generatedUIDs.size(); i++)
        {
            UIDBasedID target = m_randomSupport.pick(m_generatedUIDs);
            DvEHRURI targetUri = new DvEHRURI("ehr://" + target.getValue());
            Link link = new Link(new DvText("I'm feeling lucky"), new DvText("generated link"), targetUri);
            links.add(link);
        }
        return links;
    }

}
