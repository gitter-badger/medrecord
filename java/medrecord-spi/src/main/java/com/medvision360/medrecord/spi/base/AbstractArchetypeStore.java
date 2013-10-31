package com.medvision360.medrecord.spi.base;

import java.io.IOException;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.support.identification.ArchetypeID;

public abstract class AbstractArchetypeStore implements ArchetypeStore
{
    protected String m_name;

    public AbstractArchetypeStore(String name)
    {
        m_name = name;
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
    }

    @Override
    public void initialize() throws IOException
    {
    }

    @Override
    public void clear() throws IOException
    {
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

    @Override
    public String getName()
    {
        return m_name;
    }
}
