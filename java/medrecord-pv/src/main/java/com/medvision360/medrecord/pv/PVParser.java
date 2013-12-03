/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright (c) 2008-2010 Cambio Healthcare Systems, Sweden,
 *   Copyright (c) 2013 MEDvision360, The Netherlands.
 *   Licensed under the MPL 1.1/GPL 2.0/LGPL 2.1.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 * @author Rong Chen <rong.acode@gmail.com>
 * @author Erik Sundvall
 */
package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import com.medvision360.medrecord.api.exceptions.CannotMaintainSortException;
import com.medvision360.medrecord.rmutil.Node;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.spi.Terminology;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTimeParser;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

import static com.google.common.base.Preconditions.checkNotNull;

// based on XMLBinding from reference implementation

@SuppressWarnings("rawtypes")
public class PVParser extends AbstractPVParser
{
    /*
    Accepts input such as
        {
          "[unittest-EHR-COMPOSITION.composition.v1]/archetype_node_id" : "at0001",
          "[unittest-EHR-COMPOSITION.composition.v1]/content/collection_type" : "LIST",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items/collection_type" : "LIST",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0004][1]/rm_entity" : "Element",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0004][1]/name/value" : "header",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0004][1]/value/value" : "date",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0005][2]/rm_entity" : "Element",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0005][2]/name/value" : "value",
          "[unittest-EHR-COMPOSITION.composition.v1]/content[unittest-EHR-ADMIN_ENTRY.date.v2][1]/data[at0003]/items[at0005][2]/value/value" : "2008-05-17"
        }
    Parsing happens in a few steps:
    - convert from bytes to a Jackson JsonNode structure
    - convert from the JsonNode structure to a tree of Nodes
      (this focuses on taking the path/value map, splitting the path on /,
       and parsing out [atXXX] and [1] at the same time)
    - convert from the tree of Nodes into a "value map" tree of Map<String,Object>
      (this value map structure is what is used by the rm-builder library.
       During this step we use reflection to figure out how to go from the path part
       name to a (@FullConstructor) attribute.)
    - convert from the value map into the RM object model
      (this is done with the help of the openehr rm-builder library which does
       quite a bit of reflection.)
    
    For further inspiration, the openehr rm-skeleton module provides a good entry point for understanding how the 
    conversion magic is set up within OpenEHR land itself.
    
    todo support using archetypes to fill in fallbacks/defaults
    */

    private RMObjectBuilder m_builder;
    private CodePhrase m_language;
    private CodePhrase m_territory;
    private CodePhrase m_encoding;
    private DvCodedText m_categoryEvent;
    private String m_rmVersion = "1.0.2";

    public PVParser(Map<SystemValue, Object> systemValues)
    {
        super(((CodePhrase) systemValues.get(SystemValue.ENCODING)).getCodeString());
        m_encoding = checkNotNull((CodePhrase) systemValues.get(SystemValue.ENCODING), 
                "systemValues.ENCODING cannot be null");
        m_language = checkNotNull((CodePhrase) systemValues.get(SystemValue.LANGUAGE), 
                "systemValues.LANGUAGE cannot be null");
        m_territory = checkNotNull((CodePhrase) systemValues.get(SystemValue.TERRITORY), 
                "systemValues.TERRITORY cannot be null");

        findCategoryEvent();
        
        m_builder = RMObjectBuilder.getInstance(systemValues);
    }

    public PVParser(TerminologyService terminologyService, MeasurementService measurementService,
            CodePhrase encoding, CodePhrase language, CodePhrase territory)
    {
        super(encoding.getCodeString());

        Map<SystemValue, Object> systemValues = new HashMap<>();
        systemValues.put(SystemValue.TERMINOLOGY_SERVICE, terminologyService);
        systemValues.put(SystemValue.MEASUREMENT_SERVICE, measurementService);
        systemValues.put(SystemValue.CHARSET, encoding);
        systemValues.put(SystemValue.ENCODING, encoding);
        m_encoding = encoding;
        systemValues.put(SystemValue.LANGUAGE, language);
        m_language = language;
        systemValues.put(SystemValue.TERRITORY, territory);
        m_territory = territory;

        findCategoryEvent();
        
        m_builder = RMObjectBuilder.getInstance(systemValues);
    }

    private void findCategoryEvent()
    {
        m_categoryEvent = new DvCodedText("event", Terminology.CATEGORY_event);
    }

    public void setRmVersion(String rmVersion)
    {
        m_rmVersion = rmVersion;
    }

    @Override
    public String getMimeType()
    {
        return "application/json";
    }

    @Override
    public String getFormat()
    {
        return "json-pv";
    }

    @Override
    public boolean supports(Locatable test)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return true;
    }

    @Override
    protected void parseCollection(Node node, Collection<Object> coll) throws IOException, ParseException
    {
        List<Node> children = node.getChildren();
        SortedMap<Integer, Node> indexMap = new TreeMap<>();

        for (Node child : children)
        {
            indexMap.put(child.getIndex(), child);
        }

        for (Node child : indexMap.values())
        {
            Object childValue = parse(child);
            if (coll instanceof SortedSet && childValue != null && !(childValue instanceof Comparable))
            {
                throw new CannotMaintainSortException(String.format(
                        "Collection at %s needs to be a SET, " +
                        "but value at path %s is of type %s which is not Comparable " +
                        "so it cannot be put into a set. Either change the collection " +
                        "to be a list or add only Comparable entities into it.",
                        node.getPath(),
                        child.getPath(),
                        childValue.getClass().getSimpleName()
                ));
            }
            coll.add(parse(child));
        }
    }

    @Override
    protected Map<Object, Object> parseMap(Node node) throws ParseException, IOException
    {
        Map<Object, Object> map = new HashMap<>();
        Collection<Node> nodes = node.getChildren();
        for (Node entry : nodes)
        {
            Node key = entry.findAttributeNode("key");
            if (key == null)
            {
                throw new ParseException(String.format("MAP Node %s without nested /key", node));
            }
            Node value = entry.findAttributeNode("value");
            if (value == null)
            {
                throw new ParseException(String.format("MAP Node %s without nested /value", node));
            }

            Object keyObject = parse(key); // recurse!
            Object valueObject = parse(value); // recurse!
            map.put(keyObject, valueObject);
        }
        return map;
    }

    @Override
    protected Object parseObject(Node node) throws ParseException, IOException
    {
        // node.level == root|archetypeNodeId
        // node.collectionType == null
        //   node.children.level == attribute
        String rmEntity = node.getRmEntity();
        if (rmEntity == null)
        {
            throw new ParseException(String.format("Missing /rm_entity for %s", node));
        }

        try
        {
            Map<String, Object> valueMap = new HashMap<>();

            parseNodeFields(node, valueMap);
            parseChildren(node, valueMap);

            Object result;
            try
            {
                result = m_builder.construct(rmEntity, valueMap);
            }
            catch (RMObjectBuildingException e)
            {
                if (shouldRetryWithGuess(e))
                {
                    rmEntity = guessRmEntity(rmEntity, node);
                    if (rmEntity == null)
                    {
                        throw e;
                    }
                    //rmEntity = guess;
                    result = m_builder.construct(rmEntity, valueMap);
                }
                else
                {
                    throw e;
                }
            }

            for (Object child : valueMap.values())
            {
                setParent(child, result);
            }

            return result;
        }
        catch (RMObjectBuildingException|InvocationTargetException|IllegalAccessException e)
        {
            throw new ParseException(String.format("Problem while parsing %s: %s", node, e.getMessage()), e);
        }
    }

    private boolean shouldRetryWithGuess(RMObjectBuildingException e)
    {
        String message = e.getMessage();
        return message != null &&
                (message.contains("type unknown") ||
                 message.contains("Missing @FullConstructor") ||
                 message.contains("type abstract"));
    }

    private void parseNodeFields(Node node, Map<String, Object> valueMap)
    {
        Node valueNode = node.findAttributeNode("value");
        if (valueNode != null)
        {
            String primitiveValue = node.getValue();
            if (primitiveValue != null)
            {
                valueMap.put("value", primitiveValue);
            }
        }

        String archetypeNodeId = node.findArchetypeNodeId();
        if (archetypeNodeId != null && !valueMap.containsKey("archetypeNodeId"))
        {
            valueMap.put("archetypeNodeId", archetypeNodeId);
        }
        
        String archetypeId = node.findArchetypeId();
        if (archetypeId != null && !valueMap.containsKey("archetypeDetails"))
        {
            ArchetypeID archetypeID = new ArchetypeID(archetypeId);
            Archetyped archetypeDetails = new Archetyped(archetypeID, m_rmVersion);
            valueMap.put("archetypeDetails", archetypeDetails);
        }
    }

    private void parseChildren(Node node, Map<String, Object> valueMap)
            throws ParseException, IOException, RMObjectBuildingException
    {
        // node.level == root|archetypeNodeId
        //   node.children.level == attribute
        String rmEntity = node.getRmEntity();
        Map<String, Class<?>> attributes;
        try
        {
            attributes = m_builder.retrieveAttribute(rmEntity);
        }
        catch (RMObjectBuildingException e)
        {
            if (shouldRetryWithGuess(e))
            {
                rmEntity = guessRmEntity(rmEntity, node);
                if (rmEntity == null)
                {
                    throw e;
                }
                attributes = m_builder.retrieveAttribute(rmEntity);
            }
            else
            {
                throw e;
            }
        }
        List<Node> children = node.getChildren();
        for (Node child : children)
        {
            //   child.level == attribute
            String attributeName = child.getAttributeName();
            attributeName = toFieldName(attributeName);

            if (attributes.containsKey(attributeName))
            {
                Node indexChild = child.firstChild();
                Node archetypeNodeIdChild = indexChild.firstChild();

                String childRmEntity = archetypeNodeIdChild.getRmEntity();
                if (childRmEntity == null)
                {
                    Class clazz = attributes.get(attributeName);
                    childRmEntity = clazz.getSimpleName();
                    archetypeNodeIdChild.setRmEntity(childRmEntity);
                }
            }

            Object value = parse(child); // recurse!
            valueMap.put(attributeName, value);
        }
        guessDefaults(rmEntity, valueMap, attributes);
    }

    private String guessRmEntity(String rmEntity, Node node)
    {
        if ("PartyProxy".equals(rmEntity))
        {
            if(node.pathMatches("/subject"))
            {
                return "PartySelf";
            }
            return "PartyIdentified";
        }
        
        if ("ObjectID".equals(rmEntity) || "UIDBasedID".equals(rmEntity))
        {
            return "HierObjectID";
        }
        
        if ("ItemStructure".equals(rmEntity))
        {
            return "ItemList";
        }
        
        if ("DataValue".equals(rmEntity))
        {
            String value = node.findValue();
            if (value == null)
            {
                Node child = node.findAttributeNode("value");
                if (child != null)
                {
                    value = child.findValue();
                }
            }
            
            if (value == null)
            {
                return null;
            }
            
            if (value.matches("^-?[0-9]+(?:\\.[0-9]+)?(?:E[0-9]+)$"))
            {
                if (node.findAttributeNode("units") != null)
                {
                    return "DvQuantity";
                }
                else if (node.findAttributeNode("numerator") != null)
                {
                    return "DvProportion";
                }
                else if (node.findAttributeNode("magnitude") != null)
                {
                    return "DvCount";
                }
                else if (node.findAttributeNode("symbol") != null)
                {
                    return "DvOrdinal";
                }
            }

            try
            {
                DvDuration.parseValue(value);
                return "DvDuration";
            } catch(IllegalArgumentException e) {}
            
            try
            {
                DvDateTimeParser.parseDateTime(value);
                return "DvDateTime";
            } catch(IllegalArgumentException e) {}
            
            try
            {
                DvDateTimeParser.parseDate(value);
                return "DvDate";
            } catch(IllegalArgumentException e) {}
            
            try
            {
                DvDateTimeParser.parseTime(value);
                return "DvTime";
            } catch(IllegalArgumentException e) {}
            
            if (node.findAttributeNode("id") != null
                    && node.findAttributeNode("issuer") != null)
            {
                return "DvIdentifier";
            }
            
            if (node.getAttributeNode("value") != null
                    && node.getAttributeNode("value").getAttributeNode("value") != null
                    && node.getAttributeNode("value").getAttributeNode("defining_code") != null)
            {
                // state is a DvValue hat takes anoter DvCodedText in the constructor
                return "DvState";
            }
            
            if (node.getAttributeNode("formalism") != null)
            {
                return "DvEncapsulated";
            }
            
            if (node.getAttributeNode("mediaType") != null)
            {
                return "DvMultimedia";
            }
            
            if (node.getAttributeNode("definingCode") != null)
            {
                return "DvCodedText";
            }

            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
            {
                return "DvBoolean";
            }

            if (value.contains("://"))
            {
                try {
                    URI uri = new URI(value);
                    if ("ehr".equals(uri.getScheme()))
                    {
                        return "DvEHRURI";
                    }
                    return "DvURI";
                }
                catch (URISyntaxException e) {}
            }

            return "DvText";
        }
        
        if ("DvEncapsulated".equals(rmEntity))
        {
            if (node.getAttributeNode("formalism") != null)
            {
                return "DvEncapsulated";
            }
            if (node.getAttributeNode("mediaType") != null)
            {
                return "DvMultimedia";
            }
            return null;
        }
        
        return null;
    }
    
    @SuppressWarnings("ConstantConditions")
    private void guessDefaults(String rmEntity, Map<String, Object> valueMap, Map<String, Class<?>> attributes)
    {
        // todo I don't know why a lot of these don't simply count as SystemValue in the RM....
        
        if (attributes.containsKey("name") && !valueMap.containsKey("name"))
        {
            Class klass = attributes.get("name");
            String className = klass.getSimpleName();
            if ("String".equals(className))
            {
                valueMap.put("name", "UNNAMED");
            }
            else if ("DvText".equals(className))
            {
                valueMap.put("name", new DvText("UNNAMED"));
            }
        }

        if (attributes.containsKey("language") && !valueMap.containsKey("language"))
        {
            Class klass = attributes.get("language");
            String className = klass.getSimpleName();
            if ("CodePhrase".equals(className))
            {
                valueMap.put("language", m_language);
            }
        }

        if (attributes.containsKey("territory") && !valueMap.containsKey("territory"))
        {
            Class klass = attributes.get("territory");
            String className = klass.getSimpleName();
            if ("CodePhrase".equals(className))
            {
                valueMap.put("territory", m_territory);
            }
        }

        if (attributes.containsKey("encoding") && !valueMap.containsKey("encoding"))
        {
            Class klass = attributes.get("encoding");
            String className = klass.getSimpleName();
            if ("CodePhrase".equals(className))
            {
                valueMap.put("encoding", m_encoding);
            }
        }

        if ("ARCHETYPED".equalsIgnoreCase(rmEntity))
        {
            if (attributes.containsKey("rmVersion") && !valueMap.containsKey("rmVersion"))
            {
                valueMap.put("rmVersion", "1.0.2");
            }
        }

        if ("COMPOSITION".equalsIgnoreCase(rmEntity))
        {
            if (m_categoryEvent != null && !valueMap.containsKey("category"))
            {
                valueMap.put("category", m_categoryEvent);
            }
            
            if (!valueMap.containsKey("composer"))
            {
                PartyIdentified composer = new PartyIdentified(null, "UNKNOWN", null);
                valueMap.put("composer", composer);
            }
        }

        if ("OBSERVATION".equalsIgnoreCase(rmEntity) ||
                "EVALUATION".equalsIgnoreCase(rmEntity) ||
                "INSTRUCTION".equalsIgnoreCase(rmEntity) ||
                "ACTION".equalsIgnoreCase(rmEntity) ||
                "ADMINENTRY".equalsIgnoreCase(rmEntity) ||
                "ADMIN_ENTRY".equalsIgnoreCase(rmEntity) ||
                "CAREENTRY".equalsIgnoreCase(rmEntity) ||
                "CARE_ENTRY".equalsIgnoreCase(rmEntity))
        {
            if (!valueMap.containsKey("subject"))
            {
                PartySelf self = new PartySelf();
                valueMap.put("subject", self);
            }
        }
    }
}
/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the 'License'); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is XMLBinding.java
 *
 * The Initial Developer of the Original Code is Rong Chen. Portions created by
 * the Initial Developer are Copyright (C) 2003-2010 the Initial Developer. All
 * Rights Reserved.
 *
 * Contributor(s): Erik Sundvall, Leo Simons
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * ***** END LICENSE BLOCK *****
 */
