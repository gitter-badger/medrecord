package com.medvision360.medrecord.spi.base;

import java.io.IOException;

import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.support.identification.HierObjectID;

public abstract class AbstractEHRStore implements EHRStore
{
    private HierObjectID m_systemID;

    public AbstractEHRStore(HierObjectID systemID)
    {
        m_systemID = systemID;
    }

    @Override
    public HierObjectID getSystemID()
    {
        return m_systemID;
    }

    @Override
    public void initialize() throws IOException
    {
    }

    @Override
    public String getName()
    {
        return m_systemID.getValue();
    }

    @Override
    public boolean supportsTransactions()
    {
        return false;
    }

    @Override
    public void begin() throws TransactionException
    {
    }

    @Override
    public void commit() throws TransactionException
    {
    }

    @Override
    public void rollback() throws TransactionException
    {
    }
}
