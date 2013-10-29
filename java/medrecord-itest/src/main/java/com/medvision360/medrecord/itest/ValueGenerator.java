package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.am.archetype.constraintmodel.primitive.CBoolean;
import org.openehr.am.archetype.constraintmodel.primitive.CDate;
import org.openehr.am.archetype.constraintmodel.primitive.CDateTime;
import org.openehr.am.archetype.constraintmodel.primitive.CDuration;
import org.openehr.am.archetype.constraintmodel.primitive.CInteger;
import org.openehr.am.archetype.constraintmodel.primitive.CReal;
import org.openehr.am.archetype.constraintmodel.primitive.CString;
import org.openehr.am.archetype.constraintmodel.primitive.CTime;
import org.openehr.am.openehrprofile.datatypes.basic.CDvState;
import org.openehr.am.openehrprofile.datatypes.basic.State;
import org.openehr.am.openehrprofile.datatypes.basic.StateMachine;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvOrdinal;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantity;
import org.openehr.am.openehrprofile.datatypes.quantity.CDvQuantityItem;
import org.openehr.am.openehrprofile.datatypes.quantity.Ordinal;
import org.openehr.am.openehrprofile.datatypes.text.CCodePhrase;
import org.openehr.rm.common.generic.PartyProxy;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.content.entry.ISMTransition;
import org.openehr.rm.datastructure.history.Event;
import org.openehr.rm.datastructure.history.History;
import org.openehr.rm.datastructure.history.PointEvent;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.basic.DvState;
import org.openehr.rm.datatypes.quantity.DvOrdinal;
import org.openehr.rm.datatypes.quantity.DvQuantity;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.demographic.PartyIdentity;
import org.openehr.rm.support.basic.Interval;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.identification.UUID;
import org.openehr.rm.support.identification.VersionTreeID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyAccess;
import org.openehr.rm.support.terminology.TerminologyService;

/**
 * Generator of random but valid RM value types.
 */
public class ValueGenerator
{
    private final static Log log = LogFactory.getLog(ValueGenerator.class);

    private Random m_random = new Random();
    private RandomSupport m_randomSupport;
    private StringGenerator m_stringGenerator;
    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;
    private int m_generatedID = 0;

    public ValueGenerator(RandomSupport randomSupport, StringGenerator stringGenerator,
            TerminologyService terminologyService, MeasurementService measurementService)
    {
        m_randomSupport = randomSupport;
        m_stringGenerator = stringGenerator;
        m_terminologyService = terminologyService;
        m_measurementService = measurementService;
    }

    public CodePhrase generateCodePhrase(CCodePhrase domainType)
    {
        TerminologyID terminology;
        if (domainType != null)
        {
            terminology = domainType.getTerminologyId();

            if (Terminology.COMPRESSION.equals(terminology))
            {
                return chooseCompression();
            }
            else if (Terminology.CHECKSUMS.equals(terminology))
            {
                return chooseChecksum();
            }
            else if (Terminology.STATUSES.equals(terminology))
            {
                return chooseStatus();
            }
            else if (Terminology.COUNTRIES.equals(terminology))
            {
                return chooseCountry();
            }
            else if (Terminology.CHARSETS.equals(terminology))
            {
                return chooseCharset();
            }
            else if (Terminology.MEDIA_TYPES.equals(terminology))
            {
                return chooseMediaType();
            }
            else if (Terminology.LANGUAGES.equals(terminology))
            {
                return chooseLanguage();
            }

            List<String> codeList = domainType.getCodeList();
            if (codeList != null && !codeList.isEmpty())
            {
                String code = m_randomSupport.pick(codeList);
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

            if (terminology.equals(Terminology.OPENEHR))
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
            terminology = Terminology.OPENEHR;
        }

        TerminologyAccess terminologyAccess = m_terminologyService.terminology(terminology.name());
        if (terminologyAccess != null)
        {
            Set<CodePhrase> codes = terminologyAccess.allCodes();
            if (codes != null && !codes.isEmpty())
            {
                return m_randomSupport.pick(codes);
            }
        }

        return new CodePhrase(terminology, "999" + m_random.nextInt(10000));
    }

    public DvQuantity generateDvQuantity(CDvQuantity domainType)
    {
        String units;
        double magnitudeValue;
        int precisionValue;

        List<CDvQuantityItem> choices = domainType.getList();
        if (choices != null && !choices.isEmpty())
        {
            CDvQuantityItem item = m_randomSupport.pick(choices);
            Interval<Double> magnitude = item.getMagnitude();
            Interval<Integer> precision = item.getPrecision();
            units = item.getUnits();

            magnitudeValue = doubleFromInterval(magnitude);
            if (magnitudeValue < 0)
            {
                magnitudeValue = 1.0;
            }
            precisionValue = integerFromInterval(precision);
            if (precisionValue < 1)
            {
                precisionValue = 1;
            }
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
            magnitudeValue = m_random.nextDouble() * 50;
            precisionValue = 1 + m_random.nextInt(4);
        }
        return new DvQuantity(units, magnitudeValue, precisionValue, m_measurementService);
    }

    public DvState generateDvState(CDvState domainType)
    {
        CodePhrase code = null;

        StateMachine machine = domainType.getValue();
        if (machine != null)
        {
            Set<State> states = machine.getStates();
            if (states != null && !states.isEmpty())
            {
                State state = m_randomSupport.pick(states);
                String codeString = state.getName();
                code = new CodePhrase(Terminology.OPENEHR, codeString);
            }
        }

        if (code == null)
        {
            code = chooseInstructionState();
        }

        DvCodedText coded = codeToText(code);
        DvState state = new DvState(coded, m_random.nextBoolean());
        return state;
    }

    public DvOrdinal generateDvOrdinal(CDvOrdinal domainType)
    {
        Set<Ordinal> choices = domainType.getList();
        CodePhrase symbol;
        int value;

        if (choices != null && !choices.isEmpty())
        {
            Ordinal choice = m_randomSupport.pick(choices);
            symbol = choice.getSymbol();
            value = choice.getValue();
        }
        else
        {
            symbol = chooseProperty();
            value = 1 + m_random.nextInt(10);
        }

        DvCodedText codedSymbol = codeToText(symbol);
        DvOrdinal ordinal = new DvOrdinal(value, codedSymbol);
        return ordinal;
    }

    public DvDate generateDate(CDate primitive)
    {
        if (primitive != null)
        {
            List<DvDate> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvDate value = m_randomSupport.pick(choices);
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
                    if (value.compareTo(lower) < 0)
                    {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0)
                    {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    public DvDateTime generateDateTime(CDateTime primitive)
    {
        if (primitive != null)
        {
            List<DvDateTime> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvDateTime value = m_randomSupport.pick(choices);
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
                    if (value.compareTo(lower) < 0)
                    {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0)
                    {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    public DvTime generateTime(CTime primitive)
    {
        if (primitive != null)
        {
            List<DvTime> choices = primitive.getList();
            if (choices != null && !choices.isEmpty())
            {
                DvTime value = m_randomSupport.pick(choices);
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
                    if (value.compareTo(lower) < 0)
                    {
                        return lower;
                    }
                }
                if (upper != null)
                {
                    if (value.compareTo(upper) > 0)
                    {
                        return upper;
                    }
                }
            }
        }
        return value;
    }

    public DvDuration generateDuration(CDuration primitive)
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

        int hours = m_random.nextInt(8);
        int minutes = m_random.nextInt(60);
        int seconds = m_random.nextInt(60);

        DvDuration value = new DvDuration(0, 0, 0, 0, hours, minutes, seconds, 0.0);
        return value;
    }

    public String generateBoolean(CBoolean primitive)
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

        return String.valueOf(m_random.nextBoolean());
    }

    public String generateInteger(CInteger primitive)
    {
        if (primitive != null)
        {
            Interval<Integer> interval = primitive.getInterval();
            if (interval != null)
            {
                return String.valueOf(integerFromInterval(interval));
            }
        }
        return String.valueOf(m_random.nextInt(10000));
    }

    public String generateReal(CReal primitive)
    {
        if (primitive != null)
        {
            Interval<Double> interval = primitive.getInterval();
            if (interval != null)
            {
                return String.valueOf(doubleFromInterval(interval));
            }
        }
        return String.valueOf(m_random.nextDouble() * 10000);
    }

    public String generateString(CString primitive)
    {
        if (primitive != null)
        {
            List<String> choices = primitive.getList();
            if (choices != null)
            {
                String value = m_randomSupport.pick(choices);
                return value;
            }
        }
        return generateString();
    }

    public int generateId()
    {
        return m_generatedID++;
    }

    public DvText generateName(String className)
    {
        CodePhrase language = chooseLanguage();
        CodePhrase charset = chooseCharset();
        return new DvText(className + "-" + generateId(), language, charset, m_terminologyService);
    }

    public String generateNodeId()
    {
        return String.format("at999%04d", generateId());
    }

    public String generateString()
    {
        int wordCount = 1 + m_random.nextInt(9);
        return m_stringGenerator.generateWords(wordCount);
    }

    public HierObjectID generateUID()
    {
        return new HierObjectID(generateUUID(), null);
    }

    public UUID generateUUID()
    {
        return new UUID(java.util.UUID.randomUUID().toString());
    }

    public ObjectVersionID generateOVID()
    {
        return new ObjectVersionID(generateUID().root(), new HierObjectID("medrecord.generator"),
                new VersionTreeID("1"));
    }

    public int integerFromInterval(Interval<Integer> interval)
    {
        if (interval == null)
        {
            return m_random.nextInt();
        }

        int lower = interval.getLower() == null ? 0 :
                Math.max(interval.getLower(), 0);
        int upper = interval.getUpper() == null ? Integer.MAX_VALUE :
                Math.max(interval.getUpper(), lower);

        if (upper == 0)
        {
            return 0;
        }

        boolean flip = false;
        if (upper < 0)
        {
            flip = true;
            upper = -upper;
        }
        int value = m_random.nextInt(upper);
        if (flip)
        {
            value = -value;
        }
        if (value < lower)
        {
            value = lower;
        }
        return value;
    }

    public double doubleFromInterval(Interval<Double> interval)
    {
        if (interval == null)
        {
            return m_random.nextDouble();
        }

        double lower = interval.getLower() == null ? 0 :
                Math.max(interval.getLower(), 0);
        double upper = interval.getUpper() == null ? Double.MAX_VALUE :
                Math.max(interval.getUpper(), lower);

        double value = m_random.nextDouble() * upper;
        if (value < lower)
        {
            value = lower;
        }
        return value;
    }

    public DvCodedText codeToText(CodePhrase code)
    {
        return codeToText(code, null);
    }

    public DvCodedText codeToText(CodePhrase code, String terminology)
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

    public PartyProxy generateSelf()
    {
        PartySelf partySelf = new PartySelf();
        return partySelf;
    }

    public PartyProxy generateSubject()
    {
        return generateSelf();
    }

    //public PartyProxy generateComposer()
    //{
    //    return makeSelf();
    //}

    public ItemList generateDescription()
    {
        ItemList description = new ItemList(generateNodeId(), generateName("item-list"), null);
        return description;
    }

    public Element generateObservationEventElement()
    {
        String nodeId = generateNodeId();
        DvText name = generateName("element");
        DvText value = new DvText(generateString());
        Element element = new Element(nodeId, name, value);
        return element;
    }

    public ItemList generateObservationEventData()
    {
        String nodeId = generateNodeId();
        DvText name = generateName("item-list");

        List<Element> items = new ArrayList<>();
        int listSize = m_randomSupport.listSize(4);
        for (int i = 0; i < listSize; i++)
        {
            items.add(generateObservationEventElement());
        }

        ItemList data = new ItemList(nodeId, name, items);
        return data;
    }

    public PointEvent<ItemList> generateEvent()
    {
        String nodeId = generateNodeId();
        DvText name = generateName("event");
        DvDateTime origin = generateDateTime(null);
        ItemList data = generateObservationEventData();
        PointEvent<ItemList> event = new PointEvent<>(nodeId, name, origin, data);
        return event;
    }

    public History<ItemList> generateObservationData()
    {
        String nodeId = generateNodeId();
        DvText name = generateName("history");
        DvDateTime origin = generateDateTime(null);
        List<Event<ItemList>> events = new ArrayList<>();

        int listSize = m_randomSupport.listSize(4);
        for (int i = 0; i < listSize; i++)
        {
            events.add(generateEvent());
        }

        //noinspection Convert2Diamond
        History<ItemList> history = new History<ItemList>(nodeId, name, origin, events);
        return history;
    }

    public ISMTransition generateIsmTransition()
    {
        DvCodedText state = codeToText(chooseInstructionState());
        DvCodedText transition = null;
        if (m_randomSupport.should(0.3))
        {
            transition = codeToText(chooseInstructionTransition());
        }
        DvCodedText step = null; // apparently should be defined in archetype somehow, but if we are here, 
        // it is clear that it wasn't
        ISMTransition ismTransition = new ISMTransition(state, transition, step, m_terminologyService);
        return ismTransition;
    }

    public Element generateIdentityElement()
    {
        String nodeId = generateNodeId();
        DvText name = generateName("identity-element");
        DvText value = new DvText(generateString());
        Element element = new Element(nodeId, name, value);
        return element;
    }

    public PartyIdentity generateIdentity()
    {
        log.debug("generateIdentity");
        DvText purpose = new DvText("legal identity");
        /*
        ItemList(String archetypeNodeId, DvText name, List<Element> items)
         */
        List<Element> items = new ArrayList<>();
        int listSize = m_randomSupport.listSize(4);
        for (int i = 0; i < listSize; i++)
        {
            items.add(generateIdentityElement());
        }
        ItemList details = new ItemList(generateNodeId(), generateName("item-list"), items);
        PartyIdentity identity = new PartyIdentity(null, generateNodeId(), purpose, null, null, null, null, details);
        return identity;
    }

    public Set<PartyIdentity> generateIdentities()
    {
        log.debug("generateIdentities");
        Set<PartyIdentity> identities = new HashSet<>();
        int listSize = m_randomSupport.listSize(2);
        for (int i = 0; i < listSize; i++)
        {
            identities.add(generateIdentity());
        }
        return identities;
    }

    public CodePhrase chooseCategory()
    {
        return choose(Terminology.CATEGORIES);
    }

    public CodePhrase chooseAttestation()
    {
        return choose(Terminology.ATTEST);
    }

    public CodePhrase chooseAudit()
    {
        return choose(Terminology.AUDIT);
    }

    public CodePhrase chooseMultimedia()
    {
        return choose(Terminology.MULTIMEDIA);
    }

    public CodePhrase chooseProperty()
    {
        return choose(Terminology.PROPERTY);
    }

    public CodePhrase chooseVersionLifecycleState()
    {
        return choose(Terminology.VERSION_LIFECYCLE);
    }

    public CodePhrase chooseParticipationFunction()
    {
        return choose(Terminology.PARTICIPATION_FUNCTION);
    }

    public CodePhrase chooseNullFlavour()
    {
        return choose(Terminology.NULL_FLAVOUR);
    }

    public CodePhrase chooseParticipationMode()
    {
        return choose(Terminology.PARTICIPATION_MODE);
    }

    public CodePhrase chooseInstructionState()
    {
        return choose(Terminology.INSTRUCTION_STATE);
    }

    public CodePhrase chooseInstructionTransition()
    {
        return choose(Terminology.INSTRUCTION_TRANSITION);
    }

    public CodePhrase chooseSubjectRelationship()
    {
        return choose(Terminology.SUBJECT_RELATIONSHIP);
    }

    public CodePhrase chooseTermMappingPurpose()
    {
        return choose(Terminology.TERM_MAPPING_PURPOSE);
    }

    public CodePhrase chooseMathFunction()
    {
        return choose(Terminology.MATH_FUNCTION);
    }

    public CodePhrase chooseSetting()
    {
        return choose(Terminology.SETTING);
    }

    public CodePhrase chooseCompression()
    {
        return choose(Terminology.COMPRESSIONS);
    }

    public CodePhrase chooseChecksum()
    {
        return choose(Terminology.CHECKSUMS);
    }

    public CodePhrase chooseStatus()
    {
        return choose(Terminology.STATUSES);
    }

    public CodePhrase chooseCountry()
    {
        return choose(Terminology.COUNTRIES);
    }

    public CodePhrase chooseCharset()
    {
        return choose(Terminology.CHARSETS);
    }

    public CodePhrase chooseMediaType()
    {
        return choose(Terminology.MEDIA_TYPES);
    }

    public CodePhrase chooseLanguage()
    {
        return choose(Terminology.LANGUAGES);
    }

    public CodePhrase choose(CodePhrase[] phrases)
    {
        return m_randomSupport.pick(phrases);
    }
}
