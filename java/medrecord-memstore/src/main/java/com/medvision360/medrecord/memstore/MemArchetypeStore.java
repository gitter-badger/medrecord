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
package com.medvision360.medrecord.memstore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.base.AbstractArchetypeStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.InUseException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.StatusException;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemArchetypeStore extends AbstractArchetypeStore
{
    private Map<ArchetypeID, WrappedArchetype> m_storage = new ConcurrentHashMap<>();
    private Map<ArchetypeID, Boolean> m_locks = new ConcurrentHashMap<>();
    
    public MemArchetypeStore(String name)
    {
        super(name);
    }
    
    public MemArchetypeStore()
    {
        this("MemArchetypeStore");
    }

    @Override
    public WrappedArchetype get(Archetyped archetypeDetails) throws NotFoundException, IOException
    {
        checkNotNull(archetypeDetails, "archetypeDetails cannot be null");
        return get(archetypeDetails.getArchetypeId());
    }

    @Override
    public WrappedArchetype get(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        synchronized(m_storage)
        {
            if (!m_storage.containsKey(archetypeID))
            {
                throw new NotFoundException(String.format("Archetype %s not found", archetypeID));
            }
            return m_storage.get(archetypeID);
        }
    }

    @Override
    public boolean has(Archetyped archetypeDetails) throws IOException
    {
        checkNotNull(archetypeDetails, "archetypeDetails cannot be null");
        return has(archetypeDetails.getArchetypeId());
    }

    @Override
    public boolean has(ArchetypeID archetypeID) throws IOException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        return m_storage.containsKey(archetypeID);
    }

    @Override
    public WrappedArchetype insert(WrappedArchetype archetype) throws DuplicateException, IOException
    {
        checkNotNull(archetype, "archetype cannot be null");
        ArchetypeID archetypeID = archetype.getArchetype().getArchetypeId();
        synchronized(m_storage)
        {
            if (m_storage.containsKey(archetypeID))
            {
                throw new DuplicateException(String.format("Archetype %s already stored", archetypeID));
            }
            m_storage.put(archetypeID, archetype);
        }
        return archetype;
    }

    @Override
    public WrappedArchetype insert(Archetype archetype) throws DuplicateException, IOException
    {
        checkNotNull(archetype, "archetype cannot be null");
        WrappedArchetype wrappedArchetype = new WrappedArchetype("", archetype);
        insert(wrappedArchetype);
        return wrappedArchetype;
    }

    @Override
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        if (m_locks.containsKey(archetypeID))
        {
            throw new InUseException(String.format("Archetype %s is in use", archetypeID));
        }
        synchronized(m_storage)
        {
            if (!m_storage.containsKey(archetypeID))
            {
                throw new NotFoundException(String.format("Archetype %s not found", archetypeID));
            }
            m_storage.remove(archetypeID);
        }
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        if (!m_storage.containsKey(archetypeID))
        {
            throw new NotFoundException(String.format("Archetype %s not found", archetypeID));
        }
        synchronized(m_locks)
        {
            if (!m_locks.containsKey(archetypeID))
            {
                m_locks.put(archetypeID, true);
            }
        }
    }

    @Override
    public Iterable<ArchetypeID> list() throws IOException
    {
        return new HashSet<>(m_storage.keySet());
    }

    @Override
    public void clear() throws IOException
    {
        m_storage.clear();
        m_locks.clear();
    }

    @Override
    public void verifyStatus() throws StatusException
    {
    }

    @Override
    public String reportStatus() throws StatusException
    {
        return String.format(String.format("%s archetypes stored", m_storage.size()));
    }
}
