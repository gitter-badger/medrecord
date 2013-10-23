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
import java.util.Collection;
import java.util.HashMap;
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
import org.openehr.rm.datatypes.text.CodePhrase;
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

    public PVParser(TerminologyService terminologyService, MeasurementService measurementService,
            CodePhrase charset, CodePhrase language)
    {
        super(charset.getCodeString());

        Map<SystemValue, Object> systemValues = new HashMap<>();
        systemValues.put(SystemValue.TERMINOLOGY_SERVICE, terminologyService);
        systemValues.put(SystemValue.MEASUREMENT_SERVICE, measurementService);
        systemValues.put(SystemValue.CHARSET, charset);
        systemValues.put(SystemValue.ENCODING, charset);
        systemValues.put(SystemValue.LANGUAGE, language);

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

            Object result = m_builder.construct(rmEntity, valueMap);

            for (Object child : valueMap.values())
            {
                setParent(child, result);
            }

            return result;
        }
        catch (RMObjectBuildingException e)
        {
            throw new ParseException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new ParseException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new ParseException(e);
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
        Map<String, Class> attributes = m_builder.retrieveAttribute(rmEntity);
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
