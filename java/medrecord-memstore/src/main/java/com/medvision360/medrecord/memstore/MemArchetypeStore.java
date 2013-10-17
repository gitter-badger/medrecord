package com.medvision360.medrecord.memstore;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

public class MemArchetypeStore implements ArchetypeStore
{
    private Map<ArchetypeID, Archetype> m_store = new ConcurrentHashMap<>();
    private Map<ArchetypeID, Boolean> m_locks = new ConcurrentHashMap<>();

    @Override
    public Archetype get(Archetyped archetypeDetails) throws NotFoundException, IOException
    {
        return get(archetypeDetails.getArchetypeId());
    }

    @Override
    public Archetype get(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        if (!m_store.containsKey(archetypeID))
        {
            throw new NotFoundException(String.format("Archetype %s not found", archetypeID));
        }
        return m_store.get(archetypeID);
    }

    @Override
    public boolean has(Archetyped archetypeDetails) throws IOException
    {
        return has(archetypeDetails.getArchetypeId());
    }

    @Override
    public boolean has(ArchetypeID archetypeID) throws IOException
    {
        return m_store.containsKey(archetypeID);
    }

    @Override
    public void insert(Archetype archetype) throws DuplicateException, IOException
    {
        ArchetypeID archetypeID = archetype.getArchetypeId();
        if (m_store.containsKey(archetypeID))
        {
            throw new DuplicateException(String.format("Archetype %s already stored", archetypeID));
        }
        m_store.put(archetypeID, archetype);
    }

    @Override
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException
    {
        if (m_locks.containsKey(archetypeID))
        {
            throw new InUseException(String.format("Archetype %s is in use", archetypeID));
        }
        if (!m_store.containsKey(archetypeID))
        {
            throw new NotFoundException(String.format("Archetype %s not found", archetypeID));
        }
        m_store.remove(archetypeID);
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        if (!m_locks.containsKey(archetypeID))
        {
            m_locks.put(archetypeID, true);
        }
    }

    @Override
    public void initialize() throws IOException
    {
    }

    @Override
    public void clear() throws IOException
    {
        m_store.clear();
        m_locks.clear();
    }
}
