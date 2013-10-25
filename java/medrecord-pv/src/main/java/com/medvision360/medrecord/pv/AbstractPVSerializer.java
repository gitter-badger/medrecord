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
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.apache.commons.lang3.StringUtils;
import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.quantity.ProportionKind;

@SuppressWarnings("rawtypes")
public abstract class AbstractPVSerializer implements LocatableSerializer
{
    private static final String OPENEHR_RM_PACKAGE = "org.openehr.rm.";
    private static final Pattern INDEX_PATH_PATTERN = Pattern.compile("^(.*?)\\[([0-9]+)\\]/$");

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

    public String toUnderscoreSeparated(String camelCase)
    {
        String[] array = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < array.length; i++)
        {
            String s = array[i];
            buf.append(s.substring(0, 1).toLowerCase());
            buf.append(s.substring(1));
            if (i != array.length - 1)
            {
                buf.append("_");
            }
        }
        return buf.toString();
    }

    protected void walk(Object obj, SerializeVisitor visitor, String path)
            throws InvocationTargetException, IllegalAccessException, IOException, SerializeException
    {
        if (obj == null)
        {
            return;
        }
        SortedMap<String, Attribute> attributes = attributeMap(obj.getClass());
        String name;
        String pathName;
        Object value;
        path = handleArchetypeNodeId(obj, path, attributes, visitor);

        String rmEntity = obj.getClass().getSimpleName();
        visitor.pair(path + "rm_entity", rmEntity);

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

            handleValue(visitor, path, pathName, value, attribute);
        }
    }

    protected void handleValue(SerializeVisitor visitor, String path, String pathName, Object value,
            Attribute attribute)
            throws IOException, InvocationTargetException, IllegalAccessException, SerializeException
    {
        String fullPath = path + pathName;

        if ("parent".equals(attribute.name()))
        {
            return;
        }
        if (value instanceof ProportionKind)
        {
            ProportionKind kind = (ProportionKind) value;
            value = BigInteger.valueOf(kind.getValue());
        }

        if (isOpenEHRRMClass(value))
        {
            walk(value, visitor, fullPath + "/"); // recurse!
        }
        else if (value instanceof Collection)
        {
            Collection coll = (Collection) value;
            String listType = collectionType(coll);
            visitor.pair(fullPath + "/collection_type", listType);
            Iterator it = coll.iterator();
            for (int i = 1; it.hasNext(); i++)
            {
                Object next = it.next();
                walk(next, visitor, indexPath(fullPath, i)); // recurse!
            }
        }
        else if (value instanceof Object[])
        {
            visitor.pair(fullPath + "/collection_type", "ARRAY");
            Object[] coll = (Object[]) value;
            for (int i = 0; i < coll.length; i++)
            {
                Object next = coll[i];
                Object index = i + 1;
                walk(next, visitor, indexPath(fullPath, index)); // recurse!
            }
        }
        else if (value instanceof Map)
        {
            visitor.pair(fullPath + "/collection_type", "MAP");
            Map map = (Map) value;
            Set entries = map.entrySet();
            Iterator it = entries.iterator();
            for (int i = 1; it.hasNext(); i++)
            {
                Map.Entry next = (Map.Entry) it.next();
                walk(next.getKey(), visitor, fullPath + "[" + i + "]/" + "key/"); // recurse!
                walk(next.getValue(), visitor, fullPath + "[" + i + "]/" + "value/"); // recurse!
            }
        }
        else
        {
            visitor.pair(fullPath, value);
        }
    }

    protected String handleArchetypeNodeId(Object obj, String path, SortedMap<String, Attribute> attributes,
            SerializeVisitor visitor)
            throws InvocationTargetException, IllegalAccessException, IOException
    {
        Object archetypeNodeId = attributes.get("archetypeNodeId");
        if (archetypeNodeId != null && pathShouldGetArchetypeNodeId(path))
        {
            Object value = get(obj, "archetypeNodeId");

            if (value != null)
            {
                if ("/".equals(path))
                {
                    visitor.pair("/archetype_node_id", value);
                    return path;
                }

                Matcher matcher = INDEX_PATH_PATTERN.matcher(path);
                if (matcher.matches())
                {
                    // change from /foo/bar/items[2]/ to /foo/bar/items[atXXX][2]/
                    String prefix = matcher.group(1);
                    String index = matcher.group(2);
                    path = prefix + "[" + value.toString() + "][" + index + "]/";
                }
                else
                {
                    // change from /foo/bar/blah/ to /foo/bar/blah[atXXX]/
                    path = path.substring(0, path.length() - 1) + "[" + value.toString() + "]/";
                }
            }
        }
        return path;
    }

    protected Object get(Object target, String name) throws InvocationTargetException, IllegalAccessException
    {
        Method getter = getter(name, target.getClass());
        if (getter == null)
        {
            return null;
        }

        Object value = getter.invoke(target);
        return value;
    }

    protected boolean pathShouldGetArchetypeNodeId(String path)
    {
        // if we already have [archetype_id_here]/ or [archetype_node_id_here]/ then we don't want to add the 
        // archetypeNodeId, but if we have foo[1]/ or foo[2]/ then we do want to add it in
        return !path.endsWith("]/") || INDEX_PATH_PATTERN.matcher(path).matches();
    }

    protected Method getter(String attributeName, Class klass)
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

    protected SortedMap<String, Attribute> attributeMap(Class rmClass)
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

    protected Constructor fullConstructor(Class klass)
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

    protected boolean isOpenEHRRMClass(Object obj)
    {
        return obj.getClass().getName().contains(OPENEHR_RM_PACKAGE);
    }

    protected String collectionType(Collection coll)
    {
        String result = "COLLECTION";

        if (coll instanceof List)
        {
            result = "LIST";
        }
        else if (coll instanceof Set)
        {
            result = "SET";
        }

        return result;
    }

    protected String indexPath(String fullPath, Object index)
    {
        return fullPath + "[" + index + "]/";
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
 *  Contributor(s): Leo Simons
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */
