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
package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.measure.unit.Unit;

import com.medvision360.medrecord.rmutil.RMUtil;
import com.medvision360.medrecord.spi.Terminology;
import org.openehr.am.archetype.constraintmodel.CComplexObject;
import org.openehr.rm.datatypes.encapsulated.DvParsable;
import org.openehr.rm.datatypes.quantity.ProportionKind;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvURI;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.LocatableRef;
import org.openehr.rm.support.identification.ObjectVersionID;

/**
 * Utilities for adapting from openehr-aom expectations/rules to opener-rm expectations/rules. There are a variety of
 * little points of friction between the archetype structures and/or serialized forms and the actual RM object model as
 * expressed in java, and this adapter helps smooth over those.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class RMAdapter extends RMUtil
{
    private ValueGenerator m_valueGenerator;

    public RMAdapter(ValueGenerator valueGenerator)
    {
        m_valueGenerator = valueGenerator;
    }

    public String findConcreteClassName(ArchetypeID archetypeID)
    {
        return findConcreteClassName(archetypeID.rmEntity());
    }

    public String findConcreteClassName(String rmType)
    {
        rmType = toRmEntityName(rmType);

        switch (rmType)
        {
            case "EVENT":
                return "POINT_EVENT";
            case "ITEM_STRUCTURE":
                return "ITEM_LIST";
            case "INTERVAL_EVENT":
                return "POINT_EVENT";
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
            case "OBSERVATION":
                switch (attributeName)
                {
                    case "otherParticipations":
                        return true;
                }
                break;
            case "INSTRUCTION":
                switch (attributeName)
                {
                    case "activities":
                    case "otherParticipations":
                        return true;
                }
                break;

            // demographic
            case "AGENT":
            case "GROUP":
            case "ORGANISATION":
            case "PERSON":
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
            case "ROLE":
                switch (attributeName)
                {
                    case "identities":
                    case "contacts":
                    case "relationships":
                    case "reverseRelationships":
                    case "capabilities":
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

    protected void forceList(Map<String, Object> valueMap, String rmTypeName)
    {
        switch (rmTypeName)
        {
            // structure
            case "ITEM_LIST":
            case "ITEM_TREE":
            case "CLUSTER":
                setToList(valueMap, "items");
                break;
            case "ITEM_TABLE":
                setToList(valueMap, "rows");
                break;
            case "HISTORY":
                setToList(valueMap, "events");
                break;

            // directory
            case "FOLDER":
                setToList(valueMap, "folders");
                setToList(valueMap, "items");
                break;

            // change control
            case "CONTRIBUTION":
                listToSet(valueMap, "versions");
                break;
            case "ORIGINAL_VERSION":
                listToSet(valueMap, "otherInputVersionUids");
                setToList(valueMap, "attestations");
                break;

            // generic
            case "ATTESTATION":
                setToList(valueMap, "items");
                break;
            case "PARTY_IDENTIFIED":
            case "PARTY_RELATED":
                setToList(valueMap, "identifiers");
                break;
            case "REVISION_HISTORY":
                setToList(valueMap, "items");
                break;
            case "REVISION_HISTORY_ITEM":
                setToList(valueMap, "audits");
                break;

            // todo resource package uses Map...

            // composition
            case "COMPOSITION":
                setToList(valueMap, "content");
                break;
            case "EVENT_CONTEXT":
                setToList(valueMap, "participations");
                break;
            case "SECTION":
                setToList(valueMap, "items");
                break;
            case "ACTION":
            case "ACTIVITY":
            case "ADMIN_ENTRY":
            case "EVALUATION":
            case "OBSERVATION":
                setToList(valueMap, "otherParticipations");
                break;
            case "INSTRUCTION":
                setToList(valueMap, "activities");
                setToList(valueMap, "otherParticipations");
                break;

            // demographic
            case "AGENT":
            case "GROUP":
            case "ORGANISATION":
            case "PERSON":
                listToSet(valueMap, "identities");
                listToSet(valueMap, "contacts");
                listToSet(valueMap, "relationships");
                listToSet(valueMap, "reverseRelationships");
                listToSet(valueMap, "roles");
                break;
            case "ROLE":
                listToSet(valueMap, "identities");
                listToSet(valueMap, "contacts");
                listToSet(valueMap, "relationships");
                listToSet(valueMap, "reverseRelationships");
                listToSet(valueMap, "roles");
                setToList(valueMap, "capabilities");
                break;
            case "CONTACT":
                setToList(valueMap, "addresses");
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void tweakValueMap(Map<String, Object> valueMap, CComplexObject definition)
    {
        String rmTypeName = toRmEntityName(definition.getRmTypeName());
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

                case "DV_PARSABLE":
                    addValue(valueMap, m_valueGenerator.generateString(null));
                    if (!valueMap.containsKey("formalism"))
                    {
                        valueMap.put("formalism", m_valueGenerator.generateString(null));
                    }
                    break;
            }
        }

        switch (rmTypeName)
        {
            // domain types
            case "CODE_PHRASE":
            case "DV_CODE_PHRASE":
                CodePhrase other = m_valueGenerator.generateCodePhrase(null);
                if (!valueMap.containsKey("terminologyId"))
                {
                    valueMap.put("terminologyId", other.getTerminologyId());
                }
                if (!valueMap.containsKey("codeString"))
                {
                    valueMap.put("codeString", other.getCodeString());
                }
                break;

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
                    valueMap.put("precision", 1);
                }
                valueMap.put("type", ProportionKind.RATIO);
                break;

            case "DV_INTERVAL":
            case "DV_INTERVAL<DV_QUANTITY>":
            case "DV_INTERVAL<DV_REAL>":
            case "DV_INTERVAL<DV_INTEGER>":
            case "DV_INTERVAL<DV_DATE_TIME>":
            case "DV_INTERVAL<DV_COUNT>":
                if (valueMap.containsKey("upper") && valueMap.containsKey("lower"))
                {
                    Object upper = valueMap.get("upper");
                    Object lower = valueMap.get("lower");
                    //noinspection unchecked
                    if (upper instanceof Comparable && lower instanceof Comparable)
                    {
                        //noinspection unchecked
                        Comparable upperC = (Comparable) upper;
                        //noinspection unchecked
                        Comparable lowerC = (Comparable) lower;
                        try
                        {
                            //noinspection unchecked
                            if (upperC.compareTo(lowerC) < 0)
                            {
                                valueMap.put("upper", lower);
                                valueMap.put("lower", upper);
                            }
                        }
                        catch (IllegalArgumentException e)
                        {
                            // will happen if, for example, using different units
                            if (upper != null)
                            {
                                valueMap.put("lower", upper);
                                valueMap.put("upper", upper);
                            }
                            else
                            {
                                valueMap.put("lower", lower);
                                valueMap.put("upper", lower);
                            }
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
            case "DV_QUANTITY":
                if (!valueMap.containsKey("magnitude"))
                {
                    valueMap.put("magnitude", 1.0);
                }
                if (!valueMap.containsKey("units"))
                {
                    valueMap.put("units", "m");
                }
                else
                {
                    String value = String.valueOf(valueMap.get("units"));
                    try
                    {
                        Unit.valueOf(value);
                    }
                    catch (IllegalArgumentException e)
                    {
                        valueMap.put("units", "m");
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
				if (!valueMap.containsKey("namespace"))
				{
					valueMap.put("namespace", "DEMOGRAPHIC");
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
                else
                {
                    Object value = valueMap.get("id");
                    if (!(value instanceof ObjectVersionID))
                    {
                        // todo not sure why this can happen
                        valueMap.put("id", m_valueGenerator.generateOVID());
                    }
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
                if (!valueMap.containsKey("ismTransition"))
                {
                    valueMap.put("ismTransition", m_valueGenerator.generateIsmTransition());
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
                if (!valueMap.containsKey("data") || valueMap.get("data") == null)
                {
                    valueMap.put("data", m_valueGenerator.generateObservationData());
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
                    //valueMap.put("activityId", m_valueGenerator.generateName("activity").getValue());
					valueMap.put("activityId", m_valueGenerator.generateNodeId());
                }
                break;

            // ehr
            case "EHR_STATUS":
                if (!valueMap.containsKey("uid"))
                {
                    // todo weirdly, UID is required for EHR_STATUS
                    valueMap.put("uid", m_valueGenerator.generateUID());
                }
                if (!valueMap.containsKey("subject"))
                {
                    valueMap.put("subject", m_valueGenerator.generateSubject());
                }
                if (!valueMap.containsKey("isModifiable"))
                {
                    valueMap.put("isModifiable", Boolean.parseBoolean(m_valueGenerator.generateBoolean(null)));
                }
                if (!valueMap.containsKey("isQueryable"))
                {
                    valueMap.put("isQueryable", Boolean.parseBoolean(m_valueGenerator.generateBoolean(null)));
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
                break;
            case "PARTY_RELATIONSHIP":
                if (!valueMap.containsKey("uid"))
                {
                    // todo weirdly, UID is required for PARTY_RELATIONSHIP
                    valueMap.put("uid", m_valueGenerator.generateUID());
                }
                if (!valueMap.containsKey("source"))
                {
                    valueMap.put("source", m_valueGenerator.generatePartyRef());
                }
                if (!valueMap.containsKey("target"))
                {
                    valueMap.put("target", m_valueGenerator.generatePartyRef());
                }
                break;
            case "PARTY_RELATED":
                Object name = valueMap.get("name");
                if (name instanceof DvText)
                {
                    name = ((DvText)name).getValue();
                }
                valueMap.put("name", String.valueOf(name));
                if (valueMap.containsKey("uid"))
                {
                    // todo weirdly, UID is required for PARTY_RELATIONSHIP
                    valueMap.put("uid", m_valueGenerator.generateUID());
                }
                if (!valueMap.containsKey("relationship"))
                {
                    valueMap.put("relationship", m_valueGenerator.codeToText(
                            m_valueGenerator.chooseSubjectRelationship()));
                }
                break;
            case "AGENT":
            case "GROUP":
            case "ORGANISATION":
            case "PERSON":
                if (!valueMap.containsKey("uid"))
                {
                    // todo weirdly, UID is required for PERSON
                    valueMap.put("uid", m_valueGenerator.generateUID());
                }
                if (valueMap.containsKey("relationships"))
                {
                    // todo, for now simply avoid invalid relationships
                    valueMap.put("relationships", null);
                }
                if (valueMap.containsKey("relationships"))
                {
                    // todo, for now simply avoid invalid relationships
                    valueMap.put("reverseRelationships", null);
                }
                // todo generating a set of valid identities seems to be difficult, not sure why
                valueMap.put("identities", m_valueGenerator.generateIdentities());
                break;
            case "ROLE":
                if (!valueMap.containsKey("uid"))
                {
                    // todo weirdly, UID is required for ROLE
                    valueMap.put("uid", m_valueGenerator.generateUID());
                }
                if (!valueMap.containsKey("performer"))
                {
                    valueMap.put("performer", m_valueGenerator.generatePartyRef());
                }
                if (valueMap.containsKey("relationships"))
                {
                    // todo, for now simply avoid invalid relationships
                    valueMap.put("relationships", null);
                }
                if (valueMap.containsKey("relationships"))
                {
                    // todo, for now simply avoid invalid relationships
                    valueMap.put("reverseRelationships", null);
                }
                break;
            
            // structure
            case "HISTORY":
                if (!valueMap.containsKey("origin"))
                {
                    valueMap.put("origin", m_valueGenerator.generateDateTime(null));
                }
                break;
            case "SECTION":
            case "CLUSTER":
                if (valueMap.containsKey("items"))
                {
                    Object items = valueMap.get("items");
                    if (items != null)
                    {
                        if (items instanceof Collection)
                        {
                            Collection collection = (Collection) items;
                            if (collection.isEmpty())
                            {
                                valueMap.put("items", null);
                            }
                        }
                    }
                }
                break;

            // other
            case "EVENT":
            case "POINT_EVENT":
            case "INTERVAL_EVENT":
                if (!valueMap.containsKey("name"))
                {
                    valueMap.put("name", m_valueGenerator.generateName("event"));
                }
                if (!valueMap.containsKey("data"))
                {
                    valueMap.put("data", m_valueGenerator.generateObservationEventData());
                }
                if (!valueMap.containsKey("time"))
                {
                    valueMap.put("time", m_valueGenerator.generateDateTime(null));
                }
                if (!valueMap.containsKey("width"))
                {
                    valueMap.put("width", m_valueGenerator.generateDuration(null));
                }
                if (!valueMap.containsKey("mathFunction"))
                {
                    valueMap.put("mathFunction", m_valueGenerator.codeToText(m_valueGenerator.chooseMathFunction()));
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

        forceList(valueMap, rmTypeName);

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
            valueMap.remove("null_flavour");
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

    protected void setToList(Map<String, Object> valueMap, String key)
    {
        if (valueMap.containsKey(key))
        {
            Object items = valueMap.get(key);
            if (items instanceof Set)
            {
                Set set = (Set) items;
                List<Object> list = new ArrayList<>();
                list.addAll(set);
                valueMap.put(key, list);
            }
        }
    }

    protected void listToSet(Map<String, Object> valueMap, String key)
    {
        if (valueMap.containsKey(key))
        {
            Object items = valueMap.get(key);
            if (items instanceof List)
            {
                List list = (List) items;
                Set<Object> set = new HashSet<>();
                set.addAll(list);
                valueMap.put(key, set);
            }
        }
    }

}
