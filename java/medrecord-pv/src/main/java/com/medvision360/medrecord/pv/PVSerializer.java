/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright (c) 2008 Cambio Healthcare Systems, Sweden, Copyright (c) 2013 MEDvision360, The Netherlands.
 *   Licensed under the MPL 1.1/GPL 2.0/LGPL 2.1.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 * @author Rong Chen <rong.acode@gmail.com>
 */
package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.quantity.ProportionKind;
import org.openehr.rm.support.identification.ArchetypeID;

// based on DADLBinding from reference implementation

public class PVSerializer implements LocatableSerializer
{
    private static final String OPENEHR_RM_PACKAGE = "org.openehr.rm.";
    private static final Pattern INDEX_PATH_PATTERN = Pattern.compile("^(.*?)\\[([0-9]+)\\]/$"); 

    @Override
    public void serialize(Locatable locatable, OutputStream os) throws IOException, SerializeException
    {
        serialize(locatable, os, "UTF-8");
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding) throws IOException, SerializeException
    {
        JsonFactory jsonFactory = new JsonFactory();
        
        JsonEncoding jsonEncoding = null;
        try
        {
            jsonEncoding = JsonEncoding.valueOf(encoding);
        } catch (IllegalArgumentException e)
        {
            JsonEncoding[] options = JsonEncoding.values();
            for (int i = 0; i < options.length; i++)
            {
                JsonEncoding option = options[i];
                if (option.getJavaName().equals(encoding))
                {
                    jsonEncoding = option;
                    break;
                }
            }
        }
        if (jsonEncoding == null)
        {
            throw new IllegalArgumentException(String.format("%s is not a valid JSON encoding", encoding));
        }

        final JsonGenerator jg = jsonFactory.createGenerator(os, jsonEncoding);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());

        Archetyped archetyped = locatable.getArchetypeDetails();
        ArchetypeID archetypeId = archetyped.getArchetypeId();
        String archetypeIdString = archetypeId.getValue();
        String archetypeNodeId = locatable.getArchetypeNodeId();
        String rmEntity = archetypeId.rmEntity();
        String uid = locatable.getUid().getValue();

        // todo I feel like not putting the archetype in the path makes things a lot easier
        //String prefix = "[" + archetypeIdString + "]/";
        String prefix = "/";

        jg.writeStartObject();
            jg.writeStringField("uid", uid);
            jg.writeStringField("rm_entity", rmEntity);
            jg.writeStringField("archetype_id", archetypeIdString);
            jg.writeStringField("archetype_node_id", archetypeNodeId);
            jg.writeObjectFieldStart("contents");
                try
                {
                    walk(
                            locatable,
                            new Visitor() {
                                @Override
                                public void pair(String path, Object value) throws IOException
                                {
                                    if (value == null)
                                    {
                                        jg.writeNullField(path);
                                    }
                                    else if (value instanceof Boolean)
                                    {
                                        jg.writeBooleanField(path, (Boolean) value);
                                    }
                                    else if (value instanceof Float)
                                    {
                                        jg.writeNumberField(path, (Float)value);
                                    }
                                    else if (value instanceof Double)
                                    {
                                        jg.writeNumberField(path, (Double)value);
                                    }
                                    else if (value instanceof Integer)
                                    {
                                        jg.writeNumberField(path, (Integer)value);
                                    }
                                    else if (value instanceof Long)
                                    {
                                        jg.writeNumberField(path, (Long)value);
                                    }
                                    else if (value instanceof Short)
                                    {
                                        jg.writeNumberField(path, (Short)value);
                                    }
                                    else
                                    {
                                        jg.writeStringField(path, value.toString());
                                    }
                                }
                            },
                            prefix
                    );
                }
                catch (InvocationTargetException e)
                {
                    throw new SerializeException("Problem walking the RM object model", e);
                }
                catch (IllegalAccessException e)
                {
                    throw new SerializeException("Problem walking the RM object model", e);
                }
        jg.writeEndObject();
        jg.writeEndObject();
        jg.close();
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
    
    private static interface Visitor
    {
        public void pair(String path, Object value) throws IOException;
    }

    private void walk(Object obj, Visitor visitor, String path)
            throws InvocationTargetException, IllegalAccessException, IOException
    {
        SortedMap<String, Attribute> attributes = attributeMap(obj.getClass());
        String name;
        String pathName;
        Object value;
        path = handleArchetypeNodeId(obj, path, attributes);
        
        Iterator<String> names = attributes.keySet().iterator();
        while (names.hasNext())
        {
            name = names.next();
            pathName = toUnderscoreSeparated(name);

            Attribute attribute = attributes.get(name);
            if (attribute.system() || "archetypeNodeId".equals(attribute.name()))
            {
                continue;
            }

            value = get(obj, name);
            if (value == null)
            {
                continue;
            }

            handleValue(visitor, path, name, pathName, value, attribute);
        }
    }

    private void handleValue(Visitor visitor, String path, String name, String pathName, Object value,
            Attribute attribute) throws IOException, InvocationTargetException, IllegalAccessException
    {
        if ("parent".equals(attribute.name()))
        {
            if (value instanceof Locatable)
            {
                Locatable parentLocatable = (Locatable) value;
                String parentUid = parentLocatable.getUid().getValue();
                visitor.pair(path + "parent/uid/value", parentUid);
                String parentArchetypeId = parentLocatable.getArchetypeDetails().getArchetypeId().getValue();
                visitor.pair(path + "parent/archetype_id/value", parentArchetypeId);
            }
        }
        else if (isOpenEHRRMClass(value) && !(value instanceof ProportionKind))
        {
            if ("archetypeDetails".equals(name) && value instanceof Archetyped)
            {
                Archetyped archetypeDetails = (Archetyped)value;
                visitor.pair(path+"archetype_id/value", archetypeDetails.getArchetypeId().getValue());
            }
            else
            {
                walk(value, visitor, path + pathName + "/"); // recurse!
            }
        }
        else if (value instanceof Collection)
        {
            Collection list = (Collection) value;
            Iterator it = list.iterator();
            for (int i = 1; it.hasNext(); i++)
            {
                Object next = it.next();
                walk(next, visitor, path + pathName + "[" + i + "]/"); // recurse!
            }
        }
        else
        {
            visitor.pair(path + pathName, value);
        }
    }

    private String handleArchetypeNodeId(Object obj, String path, SortedMap<String, Attribute> attributes)
            throws InvocationTargetException, IllegalAccessException
    {
        Object archetypeNodeId = attributes.get("archetypeNodeId");
        if (archetypeNodeId != null && pathShouldGetArchetypeNodeId(path))
        {
            Object value = get(obj, "archetypeNodeId");
            
            if (value != null)
            {
                Matcher matcher = INDEX_PATH_PATTERN.matcher(path);
                if (matcher.matches())
                {
                    // change from /foo/bar/items[2]/ to /foo/bar/items[atXXXXX][2]/
                    String prefix = matcher.group(1);
                    String index = matcher.group(2);
                    path = prefix + "[" + value.toString() + "][" + index + "]/"; 
                }
                else
                {
                    // change from /foo/bar/blah/ to /foo/bar/blah[atXXXXX]/
                    path = path.substring(0, path.length()-1) + "[" + value.toString() + "]/";
                }
            }
        }
        return path;
    }

    private Object get(Object target, String name) throws InvocationTargetException, IllegalAccessException
    {
        Method getter = getter(name, target.getClass());
        if (getter == null)
        {
            return null;
        }

        Object value = getter.invoke(target, null);
        return value;
    }

    private boolean pathShouldGetArchetypeNodeId(String path)
    {
        // if we already have [archetype_id_here]/ or [archetype_node_id_here]/ then we don't want to add the 
        // archetypeNodeId, but if we have foo[1]/ or foo[2]/ then we do want to add it in
        return !"/".equals(path) && (!path.endsWith("]/") || INDEX_PATH_PATTERN.matcher(path).matches());
    }
    
    private Method getter(String attributeName, Class klass)
    {
        Method[] methods = klass.getMethods();
        String name = "get" + attributeName.substring(0, 1).toUpperCase() +
                attributeName.substring(1);

        for (Method method : methods)
        {
            if (method.getName().equals(name))
            {
                Type[] paras = method.getParameterTypes();
                if (paras.length == 0)
                {
                    return method;
                }
            }
        }
        return null;
    }

    private SortedMap<String, Attribute> attributeMap(Class rmClass)
    {
        SortedMap<String, Attribute> map = new TreeMap<>();
        Constructor constructor = fullConstructor(rmClass);

        if (constructor == null)
        {
            throw new IllegalArgumentException("Unknown RM Class: " +
                    rmClass.getClass().getCanonicalName());
        }

        Annotation[][] annotations = constructor.getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++)
        {
            if (annotations[i].length == 0)
            {
                throw new IllegalArgumentException(
                        "missing annotation at position " + i);
            }
            Attribute attribute = (Attribute) annotations[i][0];
            map.put(attribute.name(), attribute);
        }
        return map;
    }

    private Constructor fullConstructor(Class klass)
    {
        if (klass == null)
        {
            return null;
        }
        Constructor[] array = klass.getConstructors();
        for (Constructor constructor : array)
        {
            if (constructor.isAnnotationPresent(FullConstructor.class))
            {
                return constructor;
            }
        }
        return null;
    }

    public String toUnderscoreSeparated(String camelCase) {
   		String[] array = org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(camelCase);
   		StringBuffer buf = new StringBuffer();
   		for (int i = 0; i < array.length; i++) {
   			String s = array[i];
   			buf.append(s.substring(0, 1).toLowerCase());
   			buf.append(s.substring(1));
   			if (i != array.length - 1) {
   				buf.append("_");
   			}
   		}
   		return buf.toString();
   	}
    
    private boolean isOpenEHRRMClass(Object obj)
    {
        return obj.getClass().getName().contains(OPENEHR_RM_PACKAGE);
    }
}
/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  1.1 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 * The Original Code is DADLBinding.java
 * 
 * The Initial Developer of the Original Code is Rong Chen. Portions created by
 * the Initial Developer are Copyright (C) 2003-2008 the Initial Developer. All
 * Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */
