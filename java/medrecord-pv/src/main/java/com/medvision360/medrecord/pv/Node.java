package com.medvision360.medrecord.pv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.openehr.rm.support.identification.ArchetypeID;

public class Node
{
    private String m_rmEntity;
    private String m_collectionType;
    private String m_archetypeNodeId;
    private String m_archetypeId;
    private String m_attributeName;
    private int m_index = -1;
    private Node m_parent;
    private List<Node> m_children = new ArrayList<>();
    private String m_value;
    private String m_path;
    private String m_level = "root";

    public Node()
    {
    }

    public void setRmEntity(String rmEntity)
    {
        m_rmEntity = rmEntity;
    }

    public String getRmEntity()
    {
        return m_rmEntity;
    }

    public String getCollectionType()
    {
        return m_collectionType;
    }

    public void setCollectionType(String collectionType)
    {
        m_collectionType = collectionType;
    }

    public void setArchetypeNodeId(String archetypeNodeId)
    {
        m_archetypeNodeId = archetypeNodeId;
        try
        {
            new ArchetypeID(archetypeNodeId);
            if (m_archetypeId == null)
            {
                setArchetypeId(archetypeNodeId);
            }
        }
        catch (IllegalArgumentException e)
        {
            // ignore;
        }
    }

    public String getArchetypeNodeId()
    {
        return m_archetypeNodeId;
    }

    public void setArchetypeId(String archetypeId)
    {
        ArchetypeID archetypeID = new ArchetypeID(archetypeId);
        m_archetypeId = archetypeId;
        if (m_rmEntity == null)
        {
            m_rmEntity = archetypeID.rmEntity();
        }
        if (m_archetypeNodeId == null)
        {
            m_archetypeNodeId = archetypeId;
        }
    }

    public String getArchetypeId()
    {
        return m_archetypeId;
    }

    public void setAttributeName(String attributeName)
    {
        m_attributeName = attributeName;
    }

    public String getAttributeName()
    {
        return m_attributeName;
    }

    public void setIndex(int index)
    {
        m_index = index;
    }

    public int getIndex()
    {
        return m_index;
    }

    public void setParent(Node parent)
    {
        m_parent = parent;
    }

    public Node getParent()
    {
        return m_parent;
    }

    public void addChild(Node child)
    {
        m_children.add(child);
    }

    public List<Node> getChildren()
    {
        return m_children;
    }

    public Node getAttributeNode(String attributeName)
    {
        for (Node child : m_children)
        {
            if (attributeName == null || attributeName.equals(child.m_attributeName))
            {
                return child;
            }
        }

        return null;
    }

    public Node getIndexNode(int index)
    {
        for (Node child : m_children)
        {
            if (index == -1 || index == child.m_index)
            {
                return child;
            }
        }

        return null;
    }

    public Node getArchetypeNodeIdNode(String archetypeNodeId)
    {
        for (Node child : m_children)
        {
            if (archetypeNodeId == null || archetypeNodeId.equals(child.m_archetypeNodeId))
            {
                return child;
            }
        }

        return null;
    }

    public void setValue(String value)
    {
        m_value = value;
    }

    public String getValue()
    {
        return m_value;
    }

    public void setPath(String path)
    {
        m_path = path;
    }

    public String getPath()
    {
        return m_path;
    }

    public String getLevel()
    {
        return m_level;
    }

    public void setLevel(String level)
    {
        m_level = level;
    }

    public boolean isLeaf()
    {
        return getChildren().size() == 0;
    }

    public boolean isCollection()
    {
        return getCollectionType() != null;
    }

    public String findValue()
    {
        if (m_value != null)
        {
            return m_value;
        }
        Node child = firstChild();
        if (child != null)
        {
            return child.findValue();
        }
        return null;
    }

    public Node findAttributeNode(String attributeName)
    {
        Node node = this;

        if ("attribute".equals(node.getLevel()))
        {
            node = firstChild();
        }
        if ("index".equals(node.getLevel()))
        {
            node = firstChild();
        }

        // node.level == archetypeNodeId || node.level == root

        Node result = node.getAttributeNode(attributeName);

        // result.level == attribute

        return result;
    }

    public String findArchetypeNodeId()
    {
        Node node = this;

        if ("attribute".equals(node.getLevel()))
        {
            node = firstChild();
        }
        if ("index".equals(node.getLevel()))
        {
            node = firstChild();
        }

        return node.getArchetypeNodeId();
    }

    public String findArchetypeId()
    {
        Node node = this;

        if ("attribute".equals(node.getLevel()))
        {
            node = firstChild();
        }
        if ("index".equals(node.getLevel()))
        {
            node = firstChild();
        }

        return node.getArchetypeId();
    }

    public Node firstChild()
    {
        if (m_children.size() != 0)
        {
            Node child = m_children.get(0);
            return child;
        }
        return null;
    }
    
    @SuppressWarnings("ConstantConditions")
    public boolean pathMatches(String path)
    {
        if (m_path == null)
        {
            return path == null;
        }
        if (m_path.equals(path))
        {
            return true;
        }
        
        Matcher matcher;
        
        // turn [root-archetype-id]/foo/bar into /foo/bar
        String[] paths = m_path.split("/");
        String rootArchetypeId = null;
        matcher = AbstractPVParser.ROOT_ARCHETYPE_PATTERN.matcher(paths[0]);
        if (matcher.matches())
        {
            rootArchetypeId = matcher.group(1);
            String[] paths2 = new String[paths.length-1];
            System.arraycopy(paths, 1, paths2, 0, paths2.length);
            paths = paths2;
        }
        String[] otherPaths = path.split("/");
        String otherRootArchetypeId = null;
        matcher = AbstractPVParser.ROOT_ARCHETYPE_PATTERN.matcher(otherPaths[0]);
        if (matcher.matches())
        {
            otherRootArchetypeId = matcher.group(1);
            String[] paths2 = new String[otherPaths.length-1];
            System.arraycopy(otherPaths, 1, paths2, 0, paths2.length);
            otherPaths = paths2;
        }
        
        // [root-archetype-id]/foo does not match [other-root-archetype-id/bar
        // [root-archetype-id]/foo does match /foo
        if (rootArchetypeId != null && otherRootArchetypeId != null && !rootArchetypeId.equals(otherRootArchetypeId))
        {
            return false;
        }
        
        // foo/bar/blah does not match /foo/bar/blah/blahblah
        if (paths.length != otherPaths.length)
        {
            return false;
        }

        for (int i = 0; i < paths.length; i++)
        {
            String pathPart = paths[i];
            String otherPathPart = otherPaths[i];
            
            Matcher pathPartMatcher;
            
            pathPartMatcher = AbstractPVParser.PATH_PART_PATTERN.matcher(pathPart);
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

            pathPartMatcher = AbstractPVParser.PATH_PART_PATTERN.matcher(otherPathPart);
            String otherAttributeName = otherPathPart;
            String otherArchetypeNodeIdString = null;
            int otherIndex = -1;
            if (pathPartMatcher.matches())
            {
                otherAttributeName = pathPartMatcher.group(1);
                otherArchetypeNodeIdString = pathPartMatcher.group(2);
                String indexString = pathPartMatcher.group(3);
                if (indexString != null)
                {
                    otherIndex = Integer.parseInt(indexString);
                }
            }
            
            // foo/bar does not match /foo/blah
            if (!attributeName.equals(otherAttributeName))
            {
                return false;
            }
            
            // /foo/bar[at0001] does not match /foo/bar/[at0002]
            if (archetypeNodeIdString == null)
            {
                if (otherArchetypeNodeIdString != null)
                {
                    return false;
                }
            }
            else
            {
                if (!archetypeNodeIdString.equals(otherArchetypeNodeIdString))
                {
                    return false;
                }
            }
            
            // foo/bar[1]/blah matches foo/bar/blah
            if ((index != -1 || otherIndex != -1) && index != otherIndex)
            {
                return false;
            }
        }
        
        return true;
    }

    public String toString()
    {
        String result = "" +
                (m_level == null ? "" : "{" + m_level + "} ") +
                (m_attributeName == null ? "" : m_attributeName) +
                (m_archetypeNodeId == null ? "" : "[" + m_archetypeNodeId + "]") +
                (m_index == -1 ? "" : "[" + m_index + "]") +
                (m_rmEntity == null ? "" : " (" + m_rmEntity.toUpperCase() + ")") +
                (m_collectionType == null ? "" : " <" + m_collectionType.toUpperCase() + ">") +
                (m_value == null ? "" : " = " + m_value) +
                (m_path == null ? "" : "\n                                                                 " + m_path);
        return result;
    }
}
