package com.medvision360.medrecord.pv;

import java.util.ArrayList;
import java.util.List;

import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;

public class Node
{
    private HierObjectID m_uid;
    private String m_rmEntity;
    private ArchetypeID m_archetypeId;
    private String m_archetypeNodeId;
    private HierObjectID m_parentUid;
    private ArchetypeID m_parentArchetypeId;
    private String m_attributeName;
    private int m_index = -1;
    private Node m_parent;
    private List<Node> children = new ArrayList<>();
    private String m_value;

    public Node() {}

    public void setUid(String uidString)
    {
        m_uid = new HierObjectID(uidString);
    }

    public HierObjectID getUid()
    {
        return m_uid;
    }

    public void setRmEntity(String rmEntity)
    {
        m_rmEntity = rmEntity;
    }

    public String getRmEntity()
    {
        return m_rmEntity;
    }

    public void setArchetypeId(String archetypeIdString)
    {
        m_archetypeId = new ArchetypeID(archetypeIdString);
    }

    public ArchetypeID getArchetypeId()
    {
        return m_archetypeId;
    }

    public void setArchetypeNodeId(String archetypeNodeId)
    {
        m_archetypeNodeId = archetypeNodeId;
    }

    public String getArchetypeNodeId()
    {
        return m_archetypeNodeId;
    }

    public void setParentUid(String uidString)
    {
        m_parentUid = new HierObjectID(uidString);
    }

    public HierObjectID getParentUid()
    {
        return m_parentUid;
    }

    public void setParentArchetypeId(String archetypeIdString)
    {
        m_parentArchetypeId = new ArchetypeID(archetypeIdString);
    }

    public ArchetypeID getParentArchetypeId()
    {
        return m_parentArchetypeId;
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
        children.add(child);
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public Node getChild(String attributeName, String archetypeNodeIdString, int index)
    {
        for (Node child : children)
        {
            if (equals(attributeName, child.m_attributeName) &&
                    equals(archetypeNodeIdString, child.m_archetypeNodeId) &&
                    index == child.m_index)
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

    private boolean equals(String orig, String other)
    {
        if (orig == null)
        {
            return other == null;
        }
        return orig.equals(other);
    }
    
    public String toString()
    {
        String result = "" +
                (m_archetypeId == null ? "" : m_archetypeId + " ") +
                (m_attributeName == null ? "" : m_attributeName) +
                (m_archetypeNodeId == null ? "" : "["+m_archetypeNodeId+"]") +
                (m_index == -1 ? "" : "["+m_index+"]") +
                (m_uid == null ? "" : " "+m_uid);
        return result;
    }
}
