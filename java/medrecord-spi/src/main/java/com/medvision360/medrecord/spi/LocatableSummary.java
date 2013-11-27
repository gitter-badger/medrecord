/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
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
