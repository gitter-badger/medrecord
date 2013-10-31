package com.medvision360.medrecord.spi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocatableSummary
{
    private UIDBasedID m_uid;
    private ArchetypeID m_archetypeID;
    private Set<ArchetypeID> m_nestedArchetypeIDs = new HashSet<>();

    public LocatableSummary(UIDBasedID uid, ArchetypeID archetypeID)
    {
        checkNotNull(uid, "uid cannot be null");
        checkNotNull(archetypeID, "archetypeID cannot be null");
        
        m_uid = uid;
        m_archetypeID = archetypeID;
    }
    
    public void add(ArchetypeID archetypeID)
    {
        m_nestedArchetypeIDs.add(archetypeID);
    }

    public UIDBasedID getUid()
    {
        return m_uid;
    }

    public ArchetypeID getArchetypeID()
    {
        return m_archetypeID;
    }

    public Iterable<ArchetypeID> getNestedArchetypeIDs()
    {
        return Collections.unmodifiableSet(m_nestedArchetypeIDs);
    }
}
