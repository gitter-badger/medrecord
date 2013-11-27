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
package com.medvision360.medrecord.spi.base;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.DisposalException;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.TransactionException;
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
    public void dispose() throws DisposalException
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
