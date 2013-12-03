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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import com.medvision360.medrecord.api.exceptions.CannotMaintainSortException;
import com.medvision360.medrecord.rmutil.Node;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.api.exceptions.ParseException;
import org.openehr.rm.common.archetyped.Locatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public abstract class AbstractPVParser extends PVReader implements LocatableParser
{
    private static final Logger log = LoggerFactory.getLogger(AbstractPVParser.class);

    protected String m_encoding;

    public AbstractPVParser(String encoding)
    {
        m_encoding = encoding;
    }

    @Override
    public Locatable parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, m_encoding);
    }

    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException, ParseException
    {
        SortedMap<String, String> pv = toMap(is);
        return parse(pv);
    }

    protected abstract void parseCollection(Node node, Collection<Object> coll) throws IOException, ParseException;

    protected abstract Map<Object, Object> parseMap(Node node) throws ParseException, IOException;

    protected abstract Object parseObject(Node node) throws ParseException, IOException;

    protected Locatable parse(SortedMap<String, String> pv) throws IOException, ParseException
    {
        Node root = new Node();
        root.setPath("/");
        Iterator<Map.Entry<String, String>> it = pv.entrySet().iterator();
        
        String rootArchetypeId = null;

        OUTER:
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            
            Matcher rootArchetypeIdMatcher = ROOT_ARCHETYPE_PATTERN.matcher(key);
            if (rootArchetypeIdMatcher.matches())
            {
                String newArchetypeId = rootArchetypeIdMatcher.group(1);
                if (rootArchetypeId != null && !rootArchetypeId.equals(newArchetypeId))
                {
                    throw new ParseException(String.format("Multiple root archetypes: %s and %s",
                            rootArchetypeId, newArchetypeId));
                }
                rootArchetypeId = newArchetypeId;
                root.setPath("["+rootArchetypeId+"]/");
                if(root.getArchetypeId() == null)
                {
                    root.setArchetypeId(rootArchetypeId);
                }
                key = rootArchetypeIdMatcher.group(2);
            }

            String[] path = key.split("/");
            String parentPath = root.getPath().substring(0, root.getPath().length() - 1);
            String currentPath = parentPath;

            Node current = root;

            for (int i = 0; i < path.length; i++)
            {
                String pathPart = path[i].trim();
                if ("".equals(pathPart))
                {
                    continue;
                }

                currentPath += "/" + pathPart;

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

                Node newNode = makeNode(current, parentPath, attributeName, archetypeNodeIdString, index);

                current = newNode;
                parentPath = currentPath;
            }

            current.setValue(value);
        }

        transform(root);

        Object result = parse(root);
        if (!(result instanceof Locatable))
        {
            String className = result == null ? "null" : result.getClass().getSimpleName();
            throw new ParseException(String.format("Got a %s while parsing, which is not a locatable",
                    className));
        }
        return (Locatable) result;
    }

    protected Node makeNode(Node current, String parentPath, String attributeName, String archetypeNodeIdString,
            int index)
    {
        String fullPath = parentPath + "/" + attributeName;
        String path;

        Node attributeNode = current.getAttributeNode(attributeName);
        if (attributeNode == null)
        {
            attributeNode = new Node();
            path = fullPath;
            attributeNode.setPath(path);
            attributeNode.setAttributeName(attributeName);
            attributeNode.setParent(current);
            attributeNode.setLevel("attribute");
            current.addChild(attributeNode);
        }

        Node indexNode = attributeNode.getIndexNode(index);
        if (indexNode == null)
        {
            indexNode = new Node();
            path = fullPath;
            if (index != -1)
            {
                path += "[" + index + "]";
            }
            attributeNode.setPath(path);
            indexNode.setParent(attributeNode);
            indexNode.setIndex(index);
            indexNode.setLevel("index");
            attributeNode.addChild(indexNode);
        }

        Node nodeIdNode = indexNode.getArchetypeNodeIdNode(archetypeNodeIdString);
        if (nodeIdNode == null)
        {
            nodeIdNode = new Node();
            path = fullPath;
            if (archetypeNodeIdString != null)
            {
                path += "[" + archetypeNodeIdString + "]";
            }
            if (index != -1)
            {
                path += "[" + index + "]";
            }
            nodeIdNode.setPath(path);
            nodeIdNode.setParent(indexNode);
            nodeIdNode.setArchetypeNodeId(archetypeNodeIdString);
            nodeIdNode.setLevel("archetypeNodeId");
            indexNode.addChild(nodeIdNode);
        }

        return nodeIdNode;
    }

    protected void transform(Node node)
    {
        Iterator<Node> it = node.getChildren().iterator();
        transformChildren(node, it); // recurse!
    }

    protected void transformChildren(Node node, Iterator<Node> it)
    {
        while (it.hasNext())
        {
            Node child = it.next();
            // child.level == archetypeNodeId
            String attributeName = child.getAttributeName();
            String value;

            if ("archetype_node_id".equals(attributeName))
            {
                value = child.findValue();
                node.setArchetypeNodeId(value);
                child.setParent(null);
                it.remove();
                continue;
            }
            else if ("rm_entity".equals(attributeName))
            {
                value = child.findValue();
                node.setRmEntity(value);
                child.setParent(null);
                it.remove();
                continue;
            }
            else if ("collection_type".equals(attributeName))
            {
                value = child.findValue();
                Node indexNode = node.getParent();
                Node attributeNode = indexNode.getParent();
                attributeNode.setCollectionType(value);
                //node.setCollectionType(value);
                child.setParent(null);
                it.remove();
                continue;
            }

            transform(child); // recurse!
        }
    }

    protected Object parse(Node node) throws IOException, ParseException
    {
        if (node == null)
        {
            return null;
        }

        if (node.isLeaf())
        {
            return node.findValue();
        }

        if (node.isCollection())
        {
            return parseCollection(node); // recurse!
        }

        String level = node.getLevel();
        if ("root".equals(level))
        {
            return parseObject(node); // recurse!
        }
        if ("attribute".equals(level))
        {
            Node indexNode = node.firstChild();
            return parse(indexNode); // recurse!
        }
        if ("index".equals(level))
        {
            Node nodeIdNode = node.firstChild();
            return parse(nodeIdNode);
        }
        if ("archetypeNodeId".equals(level))
        {
            return parseObject(node);
        }
        throw new ParseException(String.format("Internal error: unrecognized level %s", level));
    }

    protected Object parseCollection(Node node) throws IOException, ParseException
    {
        String collectionType = node.getCollectionType();
        if ("MAP".equalsIgnoreCase(collectionType))
        {
            Map<Object, Object> map = parseMap(node); // recurse!
            return map;
        }
        else if ("SET".equalsIgnoreCase(collectionType))
        {
            Set<Object> set = new TreeSet<>();
            try
            {
                parseCollection(node, set); // recurse!
            }
            catch (CannotMaintainSortException e)
            {
                log.warn("Cannot created a sorted set, falling back to unsorted set", e);
                set = new HashSet<>();
                parseCollection(node, set); // recurse!
            }
            return set;
        }
        else if ("ARRAY".equalsIgnoreCase(collectionType))
        {
            List<Object> list = new ArrayList<>();
            parseCollection(node, list); // recurse!
            return list;
        }
        else
        {
            List<Object> list = new ArrayList<>();
            parseCollection(node, list); // recurse!
            return list;
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
