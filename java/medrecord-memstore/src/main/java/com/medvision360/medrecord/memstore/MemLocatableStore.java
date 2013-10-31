/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.memstore;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableSet;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemLocatableStore extends AbstractLocatableStore implements LocatableStore
{
    private Map<HierObjectID, Locatable> m_storage = new ConcurrentHashMap<>();
    private Map<EHR, Set<Locatable>> m_ehrMap = new ConcurrentHashMap<>();

    public MemLocatableStore(String name, LocatableSelector locatableSelector)
    {
        super(name, locatableSelector);
    }

    public MemLocatableStore(String name)
    {
        this(name, LocatableSelectorBuilder.any());
    }

    public MemLocatableStore()
    {
        this("MemLocatableStore");
    }

    @Override
    public Locatable get(HierObjectID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");

        Locatable result = m_storage.get(id);
        if (result == null)
        {
            throw notFound(id);
        }
        return result;
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException
    {
        checkNotNull(locatable, "locatable cannot be null");
        HierObjectID hierObjectID = getHierObjectID(locatable);
        ensureNotFound(hierObjectID);

        m_storage.put(hierObjectID, locatable);

        return locatable;
    }

    @Override
    public Locatable insert(EHR ehr, Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(ehr, "ehr cannot be null");
        checkNotNull(locatable, "locatable cannot be null");
        locatable = insert(locatable); // ensures UID
        
        if (!m_ehrMap.containsKey(ehr))
        {
            Set<Locatable> locatables = new HashSet<>();
            m_ehrMap.put(ehr, locatables);
        }
        m_ehrMap.get(ehr).add(locatable);
        return locatable;
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException
    {
        checkNotNull(locatable, "locatable cannot be null");
        HierObjectID hierObjectID = getHierObjectID(locatable);
        ensureFound(hierObjectID);

        m_storage.put(hierObjectID, locatable);

        return locatable;
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        ensureFound(id);

        m_storage.remove(id);
    }

    @Override
    public boolean has(HierObjectID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        return m_storage.containsKey(id);
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException
    {
        return ImmutableSet.copyOf(m_storage.keySet());
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR) throws IOException, NotFoundException
    {
        checkNotNull(EHR, "EHR cannot be null");
        return doList(EHR, null);
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR, String rmEntity) throws IOException, NotFoundException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(rmEntity, "rmEntity cannot be null");
        return doList(EHR, rmEntity);
    }

    private Iterable<HierObjectID> doList(EHR EHR, String rmEntity) throws NotFoundException
    {
        if (!m_ehrMap.containsKey(EHR))
        {
            throw new NotFoundException(String.format("EHR %s not found", EHR));
        }
        Set<Locatable> locatables = m_ehrMap.get(EHR);
        Set<HierObjectID> result = new HashSet<>();
        for (Locatable locatable : locatables)
        {
            if (rmEntity != null)
            {
                Archetyped archetypeDetails = locatable.getArchetypeDetails();
                ArchetypeID archetypeID = archetypeDetails.getArchetypeId();
                String locatableRmEntity = archetypeID.rmEntity();
                if (!rmEntity.equals(locatableRmEntity))
                {
                    continue;
                }
            }
            HierObjectID locatableId = (HierObjectID) locatable.getUid();
            result.add(locatableId);
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public void clear()
            throws IOException
    {
        m_storage.clear();
    }

    @Override
    public void verifyStatus()
            throws StatusException
    {
    }

    @Override
    public String reportStatus()
            throws StatusException
    {
        return String.format(String.format("%s locatables stored", m_storage.size()));
    }

    ///
    /// Helpers
    ///

    protected void ensureNotFound(HierObjectID hierObjectID)
            throws DuplicateException
    {
        if (m_storage.containsKey(hierObjectID))
        {
            throw duplicate(hierObjectID);
        }
    }

    protected void ensureFound(HierObjectID hierObjectID)
            throws NotFoundException
    {
        if (!m_storage.containsKey(hierObjectID))
        {
            throw notFound(hierObjectID);
        }
    }
}
