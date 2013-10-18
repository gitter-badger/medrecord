/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.memstore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableSet;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.VersionTreeID;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemLocatableStore extends AbstractLocatableStore
{
    private HierObjectID m_systemId;
    private Map<HierObjectID, Locatable> m_storage = new ConcurrentHashMap<>();
    private Map<ObjectVersionID, Locatable> m_versions = new ConcurrentHashMap<>();
    private long v = 1; // VersionTreeID: version cannot start with 0

    public MemLocatableStore(String name, LocatableSelector locatableSelector)
    {
        super(name, locatableSelector);
        m_systemId = new HierObjectID(name);
    }

    public MemLocatableStore(String name)
    {
        this(name, LocatableSelectorBuilder.any());
    }

    public MemLocatableStore()
    {
        this("MemStore");
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
    public Locatable get(ObjectVersionID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");

        Locatable result = m_versions.get(id);
        if (result == null)
        {
            throw notFound(id);
        }
        return result;
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");

        List<Locatable> result = new ArrayList<>();
        Collection<Locatable> all = m_versions.values();
        for (Locatable locatable : all)
        {
            if (locatable.getUid().equals(id))
            {
                result.add(locatable);
            }
        }
        if (result.size() == 0)
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
        storeVersion(locatable);

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
        storeVersion(locatable);

        return locatable;
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        ensureFound(id);

        m_storage.remove(id);

        List<ObjectVersionID> toDelete = new ArrayList<>();
        Set<Map.Entry<ObjectVersionID, Locatable>> entrySet = m_versions.entrySet();
        for (Map.Entry<ObjectVersionID, Locatable> entry : entrySet)
        {
            Locatable locatable = entry.getValue();
            HierObjectID hierObjectID = getHierObjectID(locatable);
            if (hierObjectID.equals(id))
            {
                ObjectVersionID objectVersionID = entry.getKey();
                toDelete.add(objectVersionID);
            }
        }
        for (ObjectVersionID objectVersionID : toDelete)
        {
            m_versions.remove(objectVersionID);
        }
    }

    @Override
    public boolean has(HierObjectID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        return m_storage.containsKey(id);
    }

    @Override
    public boolean has(ObjectVersionID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        return m_versions.containsKey(id);
    }

    @Override
    public boolean hasAny(ObjectVersionID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        UID objectID = id.objectID();
        HierObjectID hierObjectID = new HierObjectID(objectID, null);
        return has(hierObjectID);
    }

    @Override
    public void delete(ObjectVersionID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        ensureFound(id);

        m_versions.remove(id);
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException
    {
        return ImmutableSet.copyOf(m_storage.keySet());
    }

    @Override
    public Iterable<ObjectVersionID> listVersions()
            throws IOException
    {
        return ImmutableSet.copyOf(m_versions.keySet());
    }

    @Override
    public void initialize()
            throws IOException
    {
    }

    @Override
    public void clear()
            throws IOException
    {
        m_storage.clear();
        m_versions.clear();
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

    protected void ensureFound(ObjectVersionID id)
            throws NotFoundException
    {
        if (!m_versions.containsKey(id))
        {
            throw notFound(id);
        }
    }

    protected VersionTreeID nextVersion()
    {
        return new VersionTreeID("" + v++);
    }

    protected ObjectVersionID newObjectVersionID(UID uid)
    {
        return new ObjectVersionID(uid, m_systemId, nextVersion());
    }

    private void storeVersion(Locatable locatable)
    {
        HierObjectID hierObjectID = getHierObjectID(locatable);
        UID uid = hierObjectID.root();
        ObjectVersionID objectVersionID = newObjectVersionID(uid);
        m_versions.put(objectVersionID, locatable);
    }

}
