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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

// based on XMLBinding from reference implementation

public class PVParser implements LocatableParser
{
    /*
    Accepts input such as
        {
          "uid" : "3c643208-ffcb-4b67-a154-a1fa778931e0",
          "rmEntity" : "ADMIN_ENTRY",
          "archetype_id" : "unittest-EHR-ADMIN_ENTRY.date.v2",
          "archetype_node_id" : "at0001",
          "contents" : {
            "/archetype_id/value" : "unittest-EHR-ADMIN_ENTRY.date.v2",
            "/data[at0002]/items[at0003][1]/name/value" : "header",
            "/data[at0002]/items[at0003][1]/value/value" : "date",
            "/data[at0002]/items[at0004][2]/name/value" : "value",
            "/data[at0002]/items[at0004][2]/value/value" : "2008-05-17",
            "/data[at0002]/name/value" : "item list",
            "/encoding/code_string" : "iso-8859-1",
            "/encoding/terminology_id/value" : "test",
            "/language/code_string" : "en",
            "/language/terminology_id/value" : "test",
            "/name/value" : "admin entry",
            "/parent/uid/value" : "8b255eff-3e7c-4b44-9efa-b354b847e7f3",
            "/parent/archetype_id/value" : "unittest-EHR-EHRSTATUS.ehrstatus.v1",
            "/provider/external_ref/id/value" : "1.3.3.1.2.42.1",
            "/provider/external_ref/type" : "ORGANISATION",
            "/provider/name" : "provider's name",
            "/subject/external_ref/id/value" : "1.2.4.5.6.12.1",
            "/subject/external_ref/type" : "PARTY",
            "/uid/value" : "3c643208-ffcb-4b67-a154-a1fa778931e0"
          }
        }
    
    Parsing happens in a few steps:
    - convert from bytes to a Jackson JsonNode structure
    - convert from the JsonNode structure to a tree of Nodes
      (this focuses on taking the path/value map, splitting the path on /,
       and parsing out [atXXXX] and [1] at the same time)
    - convert from the tree of Nodes into a "value map" tree of Map<String,Object>
      (this value map structure is what is used by the rm-builder library.
       During this step we use reflection to figure out how to go from the path part
       name to a (@FullConstructor) attribute.)
    - convert from the value map into the RM object model
      (this is done with the help of the openehr rm-builder library which does
       quite a bit of reflection.)
    
    Todo refactor the parser so that as long as you can construct the Node tree you are golden.
    This could then be also used for parsing XML and the like.
    
    For further inspiration, the openehr rm-skeleton module provides a good entry point for understanding how the 
    conversion magic is set up within OpenEHR land itself. 
    */

    private static final String PATH_PART_FIRST = "(.+?)";
    private static final String PATH_PART_ARCHETYPE_NODE_ID = "(?:\\[([a-zA-Z][a-zA-Z0-9]+)\\])?";
    private static final String PATH_PART_INDEX_ID = "(?:\\[([0-9]+)\\])??";
    private static final Pattern PATH_PART_PATTERN = Pattern.compile(
            "^"+PATH_PART_FIRST+PATH_PART_ARCHETYPE_NODE_ID+PATH_PART_INDEX_ID+"$");

    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;

    public PVParser(TerminologyService terminologyService, MeasurementService measurementService)
    {
        m_terminologyService = terminologyService;
        m_measurementService = measurementService;
    }

    @Override
    public Locatable parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, "UTF-8");
    }
    
    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException, ParseException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(is, JsonNode.class);
        
        String uidString = rootNode.get("uid").textValue();
        String rmEntity = rootNode.get("rm_entity").textValue();
        String archetypeIdString = rootNode.get("archetype_id").textValue();
        String archetypeNodeId = rootNode.get("archetype_node_id").textValue();
        JsonNode contents = rootNode.get("contents");
        Iterator<Map.Entry<String,JsonNode>> fields = contents.fields();
        SortedMap<String, String> pv = new TreeMap<>();
        while (fields.hasNext())
        {
            Map.Entry<String,JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode node = field.getValue();
            JsonNodeType nodeType = node.getNodeType();
            String value;
            switch (nodeType)
            {
                case NULL:
                    value = null;
                    break;
                case BOOLEAN:
                case NUMBER:
                case STRING:
                    value = node.asText();
                    break;
                case ARRAY:
                case BINARY:
                case OBJECT:
                case MISSING:
                case POJO:
                default:
                    throw new ParseException(String.format(
                            "Path %s value is of type %s, need a primitive",
                            key, nodeType));
            }
            pv.put(key, value);
        }
        
        return parse(uidString, rmEntity, archetypeIdString, archetypeNodeId, pv);
    }

    private Locatable parse(String uidString, String rmEntity, String archetypeIdString, String archetypeNodeId,
            SortedMap<String, String> pv) throws IOException, ParseException
    {
        Node root = new Node();
        root.setUid(uidString);
        root.setRmEntity(rmEntity);
        root.setArchetypeId(archetypeIdString);
        root.setArchetypeNodeId(archetypeNodeId);
        Iterator<Map.Entry<String,String>> it = pv.entrySet().iterator();
        
        OUTER:
        while (it.hasNext())
        {
            Map.Entry<String,String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            
            String[] path = key.split("/");
            
            Node current = root;

            for (int i = 0; i < path.length; i++)
            {
                String pathPart = path[i].trim();
                if ("".equals(pathPart))
                {
                    continue;
                }
                
                Matcher pathPartMatcher = PATH_PART_PATTERN.matcher(pathPart);
                String attributeName = pathPart;
                String archetypeNodeIdString = null;
                int index = -1;
                if (pathPartMatcher.matches())
                {
                    attributeName = pathPartMatcher.group(1);
                    archetypeNodeIdString = pathPartMatcher.group(2);
                    String indexString = pathPartMatcher.group(3);
                    if (indexString != null)
                    {
                        index = Integer.parseInt(indexString);
                    }
                }
                
                boolean intercepted =
                    interceptArchetypeId(key, value, current, attributeName) ||
                    interceptParent(key, value, current, attributeName) ||
                    interceptUid(key, value, current, attributeName);
                if (intercepted)
                {
                    continue OUTER;
                }

                Node newNode = makeNode(current, attributeName, archetypeNodeIdString, index);
                
                current = newNode;
            }
            
            current.setValue(value);
        }
        
        Object result = parse(root);
        if (!(result instanceof Locatable))
        {
            throw new ParseException(String.format("Got a %s while parsing, which is not a locatable",
                    result.getClass().getSimpleName()));
        }
        return (Locatable) result;
    }

    private boolean interceptArchetypeId(String key, String value, Node current, String attributeName)
            throws ParseException
    {
        boolean intercept = false;

        if ("archetype_id".equals(attributeName))
        {
            if (key.endsWith("/archetype_id/value"))
            {
                current.setArchetypeId(value);
            }
            else
            {
                throw new ParseException(String.format(
                        "archetype_id sub-path %s not supported", key));
            }
            intercept = true;
        }

        return intercept;
    }

    private boolean interceptParent(String key, String value, Node current, String attributeName)
            throws ParseException
    {
        boolean intercept = false;

        if ("parent".equals(attributeName))
        {
            if (key.endsWith("/parent/uid/value"))
            {
                current.setParentUid(value);
            }
            else if (key.endsWith("/parent/archetype_id/value"))
            {
                current.setParentArchetypeId(value);
            }
            else
            {
                throw new ParseException(String.format(
                        "parent sub-path %s not supported", key));
            }
            intercept = true;
        }

        return intercept;
    }

    private boolean interceptUid(String key, String value, Node current, String attributeName)
            throws ParseException
    {
        boolean intercept = false;
        
        if ("uid".equals(attributeName))
        {
            if (key.endsWith("/uid/value"))
            {
                current.setUid(value);
            }
            else
            {
                throw new ParseException(String.format(
                        "uid sub-path %s not supported", key));
            }
            intercept = true;
        }

        return intercept;
    }

    private Node makeNode(Node current, String attributeName, String archetypeNodeIdString, int index)
    {
        Node newNode = current.getChild(attributeName, archetypeNodeIdString, index);
        if (newNode == null)
        {
            newNode = new Node();
            newNode.setAttributeName(attributeName);
            newNode.setArchetypeNodeId(archetypeNodeIdString);
            newNode.setIndex(index);
            newNode.setParent(current);
            current.addChild(newNode);
        }
        return newNode;
    }

    private Object parse(Node node) throws IOException, ParseException
    {
        if (node == null)
        {
            return node;
        }
        
        try
        {
            RMObjectBuilder builder = initializeBuilder();
            Map<String, Object> valueMap = new HashMap<>();
            Map<String, Class> attributes = builder.retrieveAttribute(node.getRmEntity());

            parseChildren(node, valueMap, attributes);
            parseNodeFields(node, valueMap);
            
            int index = node.getIndex();
            if (index != -1)
            {
                // todo we need to do some list construction when we find an index....
                return null;
            }
            
            Object result = builder.construct(node.getRmEntity(), valueMap);
            setParent(valueMap, result);
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

    private void parseChildren(Node root, Map<String, Object> valueMap, Map<String, Class> attributes)
            throws ParseException, IOException
    {
        List<Node> children = root.getChildren();
        for (Node child : children)
        {
            boolean shouldRecurse = true;
            
            String attributeName = child.getAttributeName();
            if (attributeName == null)
            {
                throw new ParseException("Child without attribute name");
            }
            attributeName = toCamelCase(attributeName); 
            
            if (attributes.containsKey(attributeName))
            {
                String rmEntity = child.getRmEntity();
                if (rmEntity == null)
                {
                    Class clazz = attributes.get(attributeName);
                    rmEntity = clazz.getSimpleName();
                    if ("ItemStructure".equals(rmEntity))
                    {
                        // todo this is abstract. We can't know which structure without looking at the archetype :/
                        rmEntity = "ItemList";
                    }
                    else if ("List".equals(rmEntity))
                    {
                        // todo list handling...
                        shouldRecurse = false;
                    }
                    else if ("Set".equals(rmEntity))
                    {
                        // todo list handling...
                        shouldRecurse = false;
                    }
                    else if ("String".equals(rmEntity))
                    {
                        shouldRecurse = false;
                    }
                    child.setRmEntity(rmEntity);
                }
            }
            else
            {
                shouldRecurse = false;
            }

            Object value;
            if (shouldRecurse)
            {
                value = parse(child); // recurse
            }
            else
            {
                value = child.getValue();
            }
            valueMap.put(attributeName, value);
        }
    }

    private void parseNodeFields(Node root, Map<String, Object> valueMap)
    {
        String primitiveValue = root.getValue();
        if (primitiveValue != null)
        {
            valueMap.put("value", primitiveValue);
        }

        ArchetypeID archetypeID = root.getArchetypeId();
        if (archetypeID != null)
        {
            valueMap.put("archetypeDetails", new Archetyped(archetypeID, "1.0.2"));
        }

        HierObjectID uid = root.getUid();
        if (uid != null)
        {
            valueMap.put("uid", uid);
        }

        // HierObjectID parentId = root.getParentUid();
        // if (parentId != null)
        // {
        //    there's not really a place to put this -- would need to link-up by doing a database query! 
        // }

        String archetypeNodeId = root.getArchetypeNodeId();
        if (archetypeNodeId != null)
        {
            valueMap.put("archetypeNodeId", archetypeNodeId);
        }
    }

    private void setParent(Map<String, Object> valueMap, Object result)
            throws IllegalAccessException, InvocationTargetException
    {
        for (Object childValue : valueMap.values())
        {
            if (childValue instanceof Pathable && result instanceof Pathable)
            {
                try
                {
                    Method setParent = childValue.getClass().getMethod("setParent", Pathable.class);
                    setParent.invoke(childValue, (Pathable)result);
                }
                catch (NoSuchMethodException e) {}
            }
        }
    }

    private RMObjectBuilder initializeBuilder() {
        CodePhrase lang = new CodePhrase("ISO_639-1", "en");
        CodePhrase charset = new CodePhrase("IANA_character-sets", "UTF-8");
        
        Map<SystemValue,Object> values = new HashMap<>();
        values.put(SystemValue.LANGUAGE, lang);
        values.put(SystemValue.ENCODING, charset);
        
        values.put(SystemValue.TERMINOLOGY_SERVICE, m_terminologyService);
        values.put(SystemValue.MEASUREMENT_SERVICE, m_measurementService);   
        return RMObjectBuilder.getInstance(values);
   	}
    
    
    public String toCamelCase(String underScoreSeperated) {
        String[] array = underScoreSeperated.split("_");
   		StringBuffer buf = new StringBuffer();
   		for (int i = 0; i < array.length; i++) {
   			String s = array[i];
   			buf.append(s.substring(0, 1).toUpperCase());
   			buf.append(s.substring(1));
   		}
   		return buf.toString();
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
 * Contributor(s): Erik Sundvall
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * ***** END LICENSE BLOCK *****
 */
