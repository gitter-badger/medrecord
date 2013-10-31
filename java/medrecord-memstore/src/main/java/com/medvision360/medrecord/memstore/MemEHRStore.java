package com.medvision360.medrecord.memstore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.StoredEHR;
import com.medvision360.medrecord.spi.base.AbstractEHRStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemEHRStore extends AbstractEHRStore
{
    private Map<HierObjectID, EHR> m_storage = new ConcurrentHashMap<>();
    private Map<HierObjectID, EHR> m_deletedStorage = new ConcurrentHashMap<>();
    private LocatableStore m_compositionStore;
    
    public MemEHRStore(HierObjectID systemID)
    {
        super(systemID);
        m_compositionStore = new MemLocatableStore(systemID.getValue()+"Compositions");
    }

    public MemEHRStore(String name)
    {
        this(new HierObjectID(name));
    }
    
    public MemEHRStore()
    {
        this("MemArchetypeStore");
    }

    @Override
    public EHR get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        EHR EHR;
        synchronized (m_storage)
        {
            if (!m_storage.containsKey(id))
            {
                throw new NotFoundException(String.format("EHR %s not found", id));
            }
            EHR = m_storage.get(id);
        }
        EHR = new StoredEHR(this, m_compositionStore, EHR.getEhrID(), EHR.getEhrStatus());
        return EHR;
    }

    @Override
    public EHR insert(EHR EHR) throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(EHR, "EHR cannot be null");
        HierObjectID id = EHR.getEhrID();
        synchronized (m_storage)
        {
            if (m_deletedStorage.containsKey(id) || m_storage.containsKey(id))
            {
                throw new DuplicateException(String.format("EHR %s already stored", id));
            }
            m_storage.put(id, EHR);
        }
        EHR ehr = new StoredEHR(this, m_compositionStore, EHR.getEhrID(), EHR.getEhrStatus());
        return ehr;
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        synchronized (m_storage)
        {
            synchronized (m_deletedStorage)
            {
                if (!m_storage.containsKey(id))
                {
                    if (m_deletedStorage.containsKey(id))
                    {
                        return;
                    }
                    else
                    {
                        throw new NotFoundException(String.format("EHR %s not found", id));
                    }
                }
                EHR EHR = m_storage.remove(id);
                m_deletedStorage.put(id, EHR);
            }
        }
    }

    @Override
    public void undelete(HierObjectID id) throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        synchronized (m_storage)
        {
            if (m_storage.containsKey(id))
            {
                return;
            }
            synchronized (m_deletedStorage)
            {
                if (!m_deletedStorage.containsKey(id))
                {
                    throw new NotFoundException(String.format("EHR %s not found", id));
                }
                
                EHR EHR = m_deletedStorage.remove(id);
                m_storage.put(id, EHR);
            }
        }
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        checkNotNull(id, "id cannot be null");
        return m_storage.containsKey(id);
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        return new HashSet<>(m_storage.keySet());
    }

    @Override
    public void clear() throws IOException
    {
        m_storage.clear();
        m_deletedStorage.clear();
    }

    @Override
    public void verifyStatus() throws StatusException
    {
    }

    @Override
    public String reportStatus() throws StatusException
    {
        return String.format(String.format("%s EHRs stored", m_storage.size()));
    }
}
