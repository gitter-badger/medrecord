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

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
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
import org.openehr.am.archetype.ontology.ArchetypeOntology;
import org.openehr.am.archetype.ontology.OntologyBinding;
import org.openehr.am.archetype.ontology.OntologyBindingItem;
import org.openehr.am.archetype.ontology.TermBindingItem;
import org.openehr.am.openehrprofile.datatypes.basic.CDvState;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Link;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvEHRURI;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.UIDBasedID;

/**
 * Programmatic generator of {@link Locatable} instances that match an {@link Archetype}.
 */
@SuppressWarnings("UnusedDeclaration")
public class LocatableGenerator
{
    private final static Log log = LogFactory.getLog(LocatableGenerator.class);

    private ValueGenerator m_valueGenerator;
    private RMAdapter m_rmAdapter;
    private AssertionSupport m_assertionSupport;
    private RandomSupport m_randomSupport;
    private ArchetypeStore m_archetypeStore;
    private RMObjectBuilder m_rmObjectBuilder;
    private String m_rmVersion = "1.0.2";
    private boolean m_fillOptional = true;
    private Map<UIDBasedID, Locatable> m_generated = new WeakHashMap<>();
    private Set<UIDBasedID> m_generatedUIDs = m_generated.keySet();
    private Collection<Locatable> m_generatedLocatables = m_generated.values();

    public LocatableGenerator(ArchetypeStore archetypeStore, RandomSupport randomSupport,
            AssertionSupport assertionSupport, ValueGenerator valueGenerator, RMAdapter rmAdapter,
            RMObjectBuilder rmObjectBuilder)
    {
        m_archetypeStore = archetypeStore;
        m_randomSupport = randomSupport;
        m_assertionSupport = assertionSupport;
        m_valueGenerator = valueGenerator;
        m_rmAdapter = rmAdapter;
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

    protected Object generateObject(Archetype archetype)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        ArchetypeID archetypeID = archetype.getArchetypeId();
        String archetypeName = archetypeID.getValue();
        String className = m_rmAdapter.findConcreteClassName(archetypeID);
        log.debug(String.format("Attempting to construct a %s to match %s", className, archetypeName));

        Map<String, Object> map = new HashMap<>();
        Archetyped archetypeDetails = new Archetyped(archetypeID, null, m_rmVersion);
        // uid added during transformation
        map.put("name", m_valueGenerator.generateName(className));
        map.put("archetypeDetails", archetypeDetails);
        map.put("archetypeNodeId", archetypeName);
        map.put("links", generateLinks());

        CComplexObject definition = archetype.getDefinition();
        generateMap(archetype, map, definition);

        return m_rmObjectBuilder.construct(className, map);
    }

    protected void generateMap(Archetype archetype, Map<String, Object> map, CComplexObject definition)
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
                Object value = generateAttribute(archetype, attribute, map, forceMultiple);
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

    protected Object generateAttribute(Archetype archetype, CAttribute attribute, Map<String, Object> map,
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
                CObject choice = m_randomSupport.pick(children);
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

    protected Object generateMultiple(Archetype archetype, CAttribute attribute, Map<String, Object> map)
            throws RMObjectBuildingException, GenerateException, IOException, NotFoundException
    {
        log.debug(String.format("generateMultiple %s %s", name(attribute), attribute.path()));
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
                Object childValue = generateObject(archetype, child, child.isRequired(), map);
                log.debug(String.format("Generated child %s, %s: %s", child.path(),
                        attribute.getRmAttributeName(), childValue));
                container.add(childValue);
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

    protected Object generateObject(Archetype archetype, ArchetypeConstraint object, boolean required,
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
            Object value = generatePrimitive(archetype, (CPrimitiveObject) object);
            return value;
        }
        else
        {
            throw new GenerateException(String.format("Unrecognized constraint type %s (%s) at %s",
                    object.getClass().getSimpleName(), objectName, objectPath));
        }
    }

    protected Object followArchetypeInternalRef(Archetype archetype, ArchetypeInternalRef ref, boolean required,
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
            }
            else
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

    protected Object translateConstraintRef(Archetype archetype, ConstraintRef ref)
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
                            term = term.substring(0, term.length() - 1);
                        }
                        CodePhrase phrase;
                        try
                        {
                            phrase = (CodePhrase) CodePhrase.parseValue(term);
                            log.debug(String.format("Mapped ref %s to %s", reference, phrase));
                            return phrase;
                        }
                        catch (IllegalArgumentException | ClassCastException e)
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

    protected Object generateObjectForSlot(Archetype archetype, ArchetypeSlot slot, boolean required)
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

    protected Object generateComplexObject(Archetype archetype, CComplexObject object, boolean required)
            throws IOException, GenerateException, NotFoundException, RMObjectBuildingException
    {
        String rmType = object.getRmTypeName();
        String objectPath = object.path();
        String className = m_rmAdapter.findConcreteClassName(rmType);
        Map<String, Object> map = new HashMap<>();
        log.debug(String.format("Generate complex object entity %s at %s with class %s",
                rmType, objectPath, className));
        try
        {
            generateMap(archetype, map, object);
            return m_rmObjectBuilder.construct(className, map);
        }
        catch (IOException | GenerateException | NotFoundException | RMObjectBuildingException e)
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

    protected Object generateDomainType(Archetype archetype, CDomainType domainType) throws GenerateException
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

    protected Object generatePrimitive(Archetype archetype, CPrimitiveObject primitiveObject) throws GenerateException
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

    protected Archetype chooseArchetype(ArchetypeSlot slot) throws IOException, GenerateException, NotFoundException
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
                if (!m_assertionSupport.testArchetypeAssertion(archetypeID, include))
                {
                    continue OUTER;
                }
            }

            for (Assertion exclude : excludes)
            {
                if (m_assertionSupport.testArchetypeAssertion(archetypeID, exclude))
                {
                    continue OUTER;
                }
            }
            possibilities.add(archetypeID);
        }

        if (possibilities.isEmpty())
        {
            return null;
        }

        ArchetypeID choice = m_randomSupport.pick(possibilities);
        return m_archetypeStore.get(choice);
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

    protected String name(ArchetypeConstraint constraint)
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
