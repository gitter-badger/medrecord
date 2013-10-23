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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.medvision360.medrecord.spi.exceptions.ParseException;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTimeParser;
import org.openehr.rm.datatypes.quantity.datetime.DvDuration;
import org.openehr.rm.datatypes.quantity.datetime.DvTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.datatypes.uri.DvURI;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

// based on XMLBinding from reference implementation

@SuppressWarnings("rawtypes")
public class PVParser extends AbstractPVParser
{
    /*
    Accepts input such as
        {
          "/archetype_node_id" : "at0001",
          "/rm_entity" : "Composition",
          "/archetype_details/rm_entity" : "Archetyped",
          "/archetype_details/archetype_id/rm_entity" : "ArchetypeID",
          "/archetype_details/archetype_id/value" : "unittest-EHR-COMPOSITION.composition.v1",
          "/archetype_details/rm_version" : "1.0.2",
          "/category/rm_entity" : "DvCodedText",
          "/category/defining_code/rm_entity" : "CodePhrase",
          "/category/defining_code/code_string" : "event",
          "/category/defining_code/terminology_id/rm_entity" : "TerminologyID",
          "/category/defining_code/terminology_id/value" : "test",
          "/category/language/rm_entity" : "CodePhrase",
          "/category/language/code_string" : "en",
          "/category/language/terminology_id/rm_entity" : "TerminologyID",
          "/category/language/terminology_id/value" : "test",
          "/category/value" : "event",
          "/composer/rm_entity" : "PartyIdentified",
          "/composer/external_ref/rm_entity" : "PartyRef",
          "/composer/external_ref/id/rm_entity" : "HierObjectID",
          "/composer/external_ref/id/value" : "1.3.3.1.2.42.1",
          "/composer/external_ref/type" : "ORGANISATION",
          "/composer/name" : "provider's name",
          "/content/collection_type" : "LIST",
          "/content[at0002][1]/rm_entity" : "AdminEntry",
          "/content[at0002][1]/archetype_details/rm_entity" : "Archetyped",
          "/content[at0002][1]/archetype_details/archetype_id/rm_entity" : "ArchetypeID",
          "/content[at0002][1]/archetype_details/archetype_id/value" : "unittest-EHR-ADMIN_ENTRY.date.v2",
          "/content[at0002][1]/archetype_details/rm_version" : "1.0.2",
          "/content[at0002][1]/data[at0003]/rm_entity" : "ItemList",
          "/content[at0002][1]/data[at0003]/items/collection_type" : "LIST",
          "/content[at0002][1]/data[at0003]/items[at0004][1]/rm_entity" : "Element",
          "/content[at0002][1]/data[at0003]/items[at0004][1]/name/rm_entity" : "DvText",
          "/content[at0002][1]/data[at0003]/items[at0004][1]/name/value" : "header",
          "/content[at0002][1]/data[at0003]/items[at0004][1]/value/rm_entity" : "DvText",
          "/content[at0002][1]/data[at0003]/items[at0004][1]/value/value" : "date",
          "/content[at0002][1]/data[at0003]/name/rm_entity" : "DvText",
          "/content[at0002][1]/data[at0003]/name/value" : "item list",
          "/content[at0002][1]/encoding/rm_entity" : "CodePhrase",
          "/content[at0002][1]/encoding/code_string" : "UTF-8",
          "/content[at0002][1]/encoding/terminology_id/rm_entity" : "TerminologyID",
          "/content[at0002][1]/encoding/terminology_id/value" : "test",
          "/content[at0002][1]/language/rm_entity" : "CodePhrase",
          "/content[at0002][1]/language/code_string" : "en",
          "/content[at0002][1]/language/terminology_id/rm_entity" : "TerminologyID",
          "/content[at0002][1]/language/terminology_id/value" : "test",
          "/content[at0002][1]/name/rm_entity" : "DvText",
          "/content[at0002][1]/name/value" : "admin entry 1",
          "/content[at0002][1]/provider/rm_entity" : "PartyIdentified",
          "/content[at0002][1]/provider/external_ref/rm_entity" : "PartyRef",
          "/content[at0002][1]/provider/external_ref/id/rm_entity" : "HierObjectID",
          "/content[at0002][1]/provider/external_ref/id/value" : "1.3.3.1.2.42.1",
          "/content[at0002][1]/provider/external_ref/type" : "ORGANISATION",
          "/content[at0002][1]/provider/name" : "provider's name",
          "/content[at0002][1]/subject/rm_entity" : "PartySelf",
          "/content[at0002][1]/subject/external_ref/rm_entity" : "PartyRef",
          "/content[at0002][1]/subject/external_ref/id/rm_entity" : "HierObjectID",
          "/content[at0002][1]/subject/external_ref/id/value" : "1.2.4.5.6.12.1",
          "/content[at0002][1]/subject/external_ref/type" : "PARTY",
          "/content[at0002][1]/uid/rm_entity" : "HierObjectID",
          "/content[at0002][1]/uid/value" : "1a7fb76a-7235-4dac-b4a6-f27d59e799ad",
          "/context/rm_entity" : "EventContext",
          "/context/setting/rm_entity" : "DvCodedText",
          "/context/setting/defining_code/rm_entity" : "CodePhrase",
          "/context/setting/defining_code/code_string" : "setting_code",
          "/context/setting/defining_code/terminology_id/rm_entity" : "TerminologyID",
          "/context/setting/defining_code/terminology_id/value" : "test",
          "/context/setting/language/rm_entity" : "CodePhrase",
          "/context/setting/language/code_string" : "en",
          "/context/setting/language/terminology_id/rm_entity" : "TerminologyID",
          "/context/setting/language/terminology_id/value" : "test",
          "/context/setting/value" : "setting",
          "/context/start_time/rm_entity" : "DvDateTime",
          "/context/start_time/value" : "2006-02-01T12:00:09",
          "/language/rm_entity" : "CodePhrase",
          "/language/code_string" : "en",
          "/language/terminology_id/rm_entity" : "TerminologyID",
          "/language/terminology_id/value" : "test",
          "/name/rm_entity" : "DvText",
          "/name/value" : "composition",
          "/territory/rm_entity" : "CodePhrase",
          "/territory/code_string" : "se",
          "/territory/terminology_id/rm_entity" : "TerminologyID",
          "/territory/terminology_id/value" : "test",
          "/uid/rm_entity" : "HierObjectID",
          "/uid/value" : "d7b2dd2a-e344-4fde-8176-246cc57fef93"
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
    */

    private RMObjectBuilder m_builder;
    private CodePhrase m_language;
    private CodePhrase m_territory;
    private CodePhrase m_encoding;
    private DvCodedText m_categoryEvent;

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
        
        Iterator<CodePhrase> it = terminologyService
                .terminology(TerminologyService.OPENEHR)
                .codesForGroupName("composition category", m_language.getCodeString())
                .iterator();
        while (it.hasNext())
        {
            CodePhrase code = it.next();
            if (code.getCodeString().equalsIgnoreCase("event"))
            {
                m_categoryEvent = new DvCodedText("event", code);
                break;
            }
        }
        
        m_builder = RMObjectBuilder.getInstance(systemValues);
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
            throw new ParseException(String.format("Missing /rm_type for %s", node));
        }

        try
        {
            Map<String, Object> valueMap = new HashMap<>();

            parseNodeFields(node, valueMap);
            parseChildren(node, valueMap);

            Object result = null;
            try
            {
                result = m_builder.construct(rmEntity, valueMap);
            }
            catch (RMObjectBuildingException e)
            {
                if (e.getMessage() != null && e.getMessage().contains("type unknown"))
                {
                    String guess = guessRmEntity(rmEntity, node);
                    if (guess == null)
                    {
                        throw e;
                    }
                    result = m_builder.construct(guess, valueMap);
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
        if (archetypeNodeId != null)
        {
            valueMap.put("archetypeNodeId", archetypeNodeId);
        }
    }

    private void parseChildren(Node node, Map<String, Object> valueMap)
            throws ParseException, IOException, RMObjectBuildingException
    {
        // node.level == root|archetypeNodeId
        //   node.children.level == attribute
        String rmEntity = node.getRmEntity();
        Map<String, Class> attributes = null;
        try
        {
            attributes = m_builder.retrieveAttribute(rmEntity);
        }
        catch (RMObjectBuildingException e)
        {
            if (e.getMessage() != null && e.getMessage().contains("RM type unknown"))
            {
                String guess = guessRmEntity(rmEntity, node);
                if (guess == null)
                {
                    throw e;
                }
                attributes = m_builder.retrieveAttribute(guess);
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
            attributeName = toCamelCase(attributeName);
            attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);

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
            String path = node.getPath();
            if(path.startsWith("/subject"))
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
    
    private void guessDefaults(String rmEntity, Map<String, Object> valueMap, Map<String, Class> attributes)
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
                "CAREENTRY".equalsIgnoreCase(rmEntity))
        {
            if (!valueMap.containsKey("subject"))
            {
                PartySelf self = new PartySelf();
                valueMap.put("subject", self);
            }
        }
    }

    


    private void setParent(Object child, Object parent)
            throws IllegalAccessException, InvocationTargetException
    {
        if (child instanceof Pathable && parent instanceof Pathable)
        {
            try
            {
                Method setParent = child.getClass().getMethod("setParent", Pathable.class);
                setParent.invoke(child, (Pathable) parent);
            }
            catch (NoSuchMethodException e)
            {
            }
        }
    }

    private String toCamelCase(String underScoreSeparated)
    {
        String[] array = underScoreSeparated.split("_");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++)
        {
            String s = array[i];
            buf.append(s.substring(0, 1).toUpperCase());
            buf.append(s.substring(1));
        }
        return buf.toString();
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
