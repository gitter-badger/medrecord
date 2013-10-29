package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datatypes.quantity.ProportionKind;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvURI;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.LocatableRef;

/**
 * Utilities for adapting from openehr-aom expectations/rules to opener-rm expectations/rules. There are a variety of
 * little points of friction between the archetype structures and/or serialized forms and the actual RM object model as
 * expressed in java, and this adapter helps smooth over those.
 */
public class RMAdapter
{
    private ValueGenerator m_valueGenerator;

    public RMAdapter(ValueGenerator valueGenerator)
    {
        m_valueGenerator = valueGenerator;
    }

    public String toUnderscoreSeparated(String camelCase)
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

    public String findConcreteClassName(ArchetypeID archetypeID)
    {
        return findConcreteClassName(archetypeID.rmEntity());
    }

    public String findConcreteClassName(String rmType)
    {
        rmType = toUnderscoreSeparated(rmType);

        switch (rmType)
        {
            case "EVENT":
                return "POINT_EVENT";
            case "ITEM_STRUCTURE":
                return "ITEM_LIST";
            case "OBJECT_ID":
            case "UID_BASED_ID":
                return "HIER_OBJECT_ID";
            default:
                return rmType;
        }
    }

    /**
     * Allows overriding the decision of whether to fill one child attribute out of a list of possible choices, or
     * instead provide a Collection instance.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean forceMultiple(String rmTypeName, String attributeName)
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

    @SuppressWarnings("ConstantConditions")
    public void tweakValueMap(Map<String, Object> valueMap, CComplexObject definition)
    {
        String rmTypeName = toUnderscoreSeparated(definition.getRmTypeName());
        //log.debug(String.format("Extending map for type %s", rmTypeName));

        boolean hasValue = hasValue(valueMap);
        boolean hasName = hasName(valueMap);
        if (!hasName)
        {
            addName(valueMap, rmTypeName.toLowerCase());
        }

        if (!hasValue)
        {
            switch (rmTypeName)
            {
                // primitives
                case "DV_DATE":
                    addValue(valueMap, m_valueGenerator.generateDate(null).toString());
                    break;
                case "DV_DATE_TIME":
                    addValue(valueMap, m_valueGenerator.generateDateTime(null).toString());
                    break;
                case "DV_TIME":
                    addValue(valueMap, m_valueGenerator.generateTime(null).toString());
                    break;
                case "DV_DURATION":
                    addValue(valueMap, m_valueGenerator.generateDuration(null).toString());
                    break;
                case "DV_BOOLEAN":
                    addValue(valueMap, m_valueGenerator.generateBoolean(null));
                    break;
                case "DV_COUNT":  // "DV_INTEGER":
                    addValue(valueMap, m_valueGenerator.generateInteger(null));
                    if (!valueMap.containsKey("magnitude"))
                    {
                        valueMap.put("magnitude", "1");
                    }
                    break;
                case "DV_TEXT": // "DV_STRING":
                    addValue(valueMap, m_valueGenerator.generateString(null));
                    break;

                case "DV_URI":
                    addValue(valueMap, "urn:generated:" + m_valueGenerator.generateName("uri"));
                    break;
                case "DV_EHRURI":
                case "DV_EHR_URI":
                    addValue(valueMap, "ehr://generated/" + m_valueGenerator.generateName("uri"));
                    break;
            }
        }

        switch (rmTypeName)
        {
            // domain types                    
            case "DV_CODED_TEXT":
                CodePhrase code;
                if (valueMap.containsKey("defining_code"))
                {
                    Object obj = valueMap.get("defining_code");
                    if (obj instanceof CodePhrase)
                    {
                        code = (CodePhrase) obj;
                    }
                    else
                    {
                        code = m_valueGenerator.generateCodePhrase(null);
                    }
                }
                else
                {
                    code = m_valueGenerator.generateCodePhrase(null);
                }
                valueMap.put("defining_code", code);
                if (!hasValue)
                {
                    addValue(valueMap, m_valueGenerator.codeToText(code).getValue());
                }
                break;

            case "DV_PROPORTION":
                if (!valueMap.containsKey("numerator"))
                {
                    valueMap.put("numerator", String.valueOf(m_valueGenerator.generateReal(null)));
                }
                if (!valueMap.containsKey("denominator"))
                {
                    valueMap.put("denominator", String.valueOf(m_valueGenerator.generateReal(null)));
                }
                if (!valueMap.containsKey("precision"))
                {
                    valueMap.put("precision", String.valueOf(m_valueGenerator.generateInteger(null)));
                }
                if (!valueMap.containsKey("type"))
                {
                    valueMap.put("type", ProportionKind.RATIO);
                }
                break;

            case "DV_INTERVAL":
            case "DV_INTERVAL<DV_QUANTITY>":
            case "DV_INTERVAL<DV_REAL>":
            case "DV_INTERVAL<DV_INTEGER>":
            case "DV_INTERVAL<DV_DATE_TIME>":
                if (valueMap.containsKey("upper") && valueMap.containsKey("lower"))
                {
                    Object upper = valueMap.get("upper");
                    Object lower = valueMap.get("lower");
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
                            valueMap.put("upper", lower);
                            valueMap.put("lower", upper);
                        }
                    }
                    if (upper instanceof Double && lower instanceof Double)
                    {
                        double upperD = (Double) upper;
                        double lowerD = (Double) lower;
                        if (upperD < lowerD)
                        {
                            valueMap.put("upper", lower);
                            valueMap.put("lower", upper);
                        }
                    }
                    if (upper instanceof Integer && lower instanceof Integer)
                    {
                        int upperI = (Integer) upper;
                        int lowerI = (Integer) lower;
                        if (upperI < lowerI)
                        {
                            valueMap.put("upper", lower);
                            valueMap.put("lower", upper);
                        }
                    }
                }
                break;

            // identification
//            case "ARCHETYPE_ID":
//                map.put("value", "openEHR-EHR-ADMIN_ENTRY.admin_entry.v1");
//                break;
            case "DV_IDENTIFIER":
                if (!valueMap.containsKey("issuer"))
                {
                    valueMap.put("issuer", "" + m_valueGenerator.generateString());
                }
                if (!valueMap.containsKey("assigner"))
                {
                    valueMap.put("assigner", "" + m_valueGenerator.generateString());
                }
                if (!valueMap.containsKey("id"))
                {
                    valueMap.put("id", "" + m_valueGenerator.generateId());
                }
                if (!valueMap.containsKey("type"))
                {
                    valueMap.put("type", "" + m_valueGenerator.generateString());
                }
                break;
            case "PARTY_REF":
                if (!valueMap.containsKey("id"))
                {
                    valueMap.put("id", m_valueGenerator.generateUID());
                }
                if (!valueMap.containsKey("type"))
                {
                    valueMap.put("type", "" + m_valueGenerator.generateString());
                }
                break;
            case "LOCATABLE_REF":
                if (!valueMap.containsKey("id"))
                {
                    valueMap.put("id", m_valueGenerator.generateOVID());
                }
                if (!valueMap.containsKey("namespace"))
                {
                    valueMap.put("namespace", m_valueGenerator.generateName("namespace").getValue());
                }
                if (!valueMap.containsKey("type"))
                {
                    valueMap.put("type", m_valueGenerator.generateName("type").getValue());
                }
                break;

            // composition
            case "COMPOSITION":
                if (!valueMap.containsKey("composer"))
                {
                    valueMap.put("composer", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("category"))
                {
                    valueMap.put("category", m_valueGenerator.codeToText(m_valueGenerator.chooseCategory()));
                }
                if (valueMap.containsKey("context"))
                {
                    valueMap.put("category", m_valueGenerator.codeToText(Terminology.CATEGORY_event));
                }
                valueMap.remove("parent");
                break;
            case "EVENT_CONTEXT":
                if (!valueMap.containsKey("start_time"))
                {
                    valueMap.put("start_time", m_valueGenerator.generateDateTime(null));
                }
                if (!valueMap.containsKey("setting"))
                {
                    valueMap.put("setting", m_valueGenerator.codeToText(m_valueGenerator.chooseSetting()));
                }
                valueMap.remove("participations");
                break;
            case "ACTION":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                if (!valueMap.containsKey("time"))
                {
                    valueMap.put("time", m_valueGenerator.generateDateTime(null));
                }
                break;
            case "ACTIVITY":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                if (!valueMap.containsKey("timing"))
                {
                    valueMap.put("timing", new DvParsable(m_valueGenerator.generateString(), "txt"));
                }
                if (!valueMap.containsKey("action_archetype_id"))
                {
                    valueMap.put("action_archetype_id", m_valueGenerator.generateString());
                }
                break;
            case "INSTRUCTION":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("narrative"))
                {
                    valueMap.put("narrative", new DvText(m_valueGenerator.generateString()));
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                break;
            case "ADMIN_ENTRY":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                break;
            case "EVALUATION":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                break;
            case "OBSERVATION":
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("description") || valueMap.get("description") == null)
                {
                    valueMap.put("description", m_valueGenerator.generateDescription());
                }
                break;
            case "INSTRUCTION_DETAILS":
                if (!valueMap.containsKey("instructionId"))
                {
                    valueMap.put("instructionId", new LocatableRef(
                            m_valueGenerator.generateOVID(), m_valueGenerator.generateName("namespace").getValue(),
                            m_valueGenerator
                                    .generateName("type").getValue(), null));
                }
                if (!valueMap.containsKey("activityId"))
                {
                    valueMap.put("activityId", m_valueGenerator.generateName("activity").getValue());
                }
                break;

            // demographic
            case "PARTICIPATION":
                if (!valueMap.containsKey("performer"))
                {
                    valueMap.put("performer", m_valueGenerator.generateSelf());
                }
                // todo at-term-binding-mapping isn't working for these
                //if (!map.containsKey("mode"))
                valueMap.put("mode", m_valueGenerator.codeToText(m_valueGenerator.chooseParticipationMode()));
                //if (!map.containsKey("function"))
                valueMap.put("function", m_valueGenerator.codeToText(m_valueGenerator.chooseParticipationFunction()));

            // structure
            case "ITEM_LIST":
                if (valueMap.containsKey("items"))
                {
                    Object items = valueMap.get("items");
                    if (items instanceof Set)
                    {
                        Set itemSet = (Set) items;
                        List<Object> itemList = new ArrayList<>();
                        itemList.addAll(itemSet);
                    }
                }
            case "HISTORY":
                if (!valueMap.containsKey("origin"))
                {
                    valueMap.put("origin", m_valueGenerator.generateDateTime(null));
                }
                break;

            // other
            case "EVENT":
            case "POINT_EVENT":
            case "INTERVAL_EVENT":
                if (!valueMap.containsKey("time"))
                {
                    valueMap.put("time", m_valueGenerator.generateDateTime(null));
                }
                break;
            case "DV_MULTIMEDIA":
                if (!valueMap.containsKey("mediaType"))
                {
                    valueMap.put("mediaType", m_valueGenerator.chooseMediaType());
                }
                if (!valueMap.containsKey("compressionAlgorithm"))
                {
                    valueMap.put("compressionAlgorithm", m_valueGenerator.chooseCompression());
                }
                if (!valueMap.containsKey("integrityCheckAlgorithm"))
                {
                    valueMap.put("integrityCheckAlgorithm", m_valueGenerator.chooseChecksum());
                }
                if (!valueMap.containsKey("uri"))
                {
                    valueMap.put("uri", new DvURI("urn:generated:" + m_valueGenerator.generateName("uri")));
                }

        }

        if (!valueMap.containsKey("encoding"))
        {
            valueMap.put("encoding", m_valueGenerator.chooseCharset());
        }
        if (!valueMap.containsKey("language"))
        {
            valueMap.put("language", m_valueGenerator.chooseLanguage());
        }
        if (!valueMap.containsKey("territory"))
        {
            valueMap.put("territory", m_valueGenerator.chooseCountry());
        }

        if (valueMap.containsKey("value") && valueMap.get("value") != null)
        {
            valueMap.remove("nullFlavour");
        }
        else
        {
            valueMap.put("nullFlavour", m_valueGenerator.codeToText(m_valueGenerator.chooseNullFlavour()));
        }
    }

    protected void addValue(Map<String, Object> map, Object value)
    {
        map.put("value", value);
    }

    protected boolean hasValue(Map<String, Object> map)
    {
        return map.containsKey("value");
    }

    protected void addName(Map<String, Object> map, String className)
    {
        map.put("name", m_valueGenerator.generateName(className));
    }

    protected boolean hasName(Map<String, Object> map)
    {
        return map.containsKey("name");
    }
}
