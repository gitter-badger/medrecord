/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.base;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.medvision360.medrecord.spi.AuditInfo;
import com.medvision360.medrecord.spi.AuditedService;
import com.medvision360.medrecord.spi.VersioningStore;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.changecontrol.VersionedObject;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;

public class NoopStore implements VersioningStore, XQueryStore, AuditedService
{
    @Override
    public void setAuditInfo(AuditInfo auditInfo)
    {
    }

    @Override
    public Iterable<VersionedObject<? extends Locatable>> getVersionObjects(HierObjectID id)
            throws NotFoundException, IOException
    {
        return new ArrayList<>();
    }

    @Override
    public void insert(VersionedObject<? extends Locatable> versionedLocatable)
            throws DuplicateException, NotSupportedException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.queryVersionObjects()");
    }

    @Override
    public void update(VersionedObject<? extends Locatable> versionedLocatable)
            throws NotSupportedException, NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.queryVersionObjects()");
    }

    @Override
    public Iterable<VersionedObject<? extends Locatable>> listVersionObjects(String XQuery)
            throws NotSupportedException, IOException, UnsupportedOperationException
    {
        return new ArrayList<>();
    }

    @Override
    public void queryVersionObjects(String XQuery, OutputStream os)
            throws NotSupportedException, IOException, UnsupportedOperationException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.queryVersionObjects()");
    }

    @Override
    public Locatable get(HierObjectID id)
            throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.get()");
    }

    @Override
    public Locatable get(ObjectVersionID id)
            throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.get()");
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id)
            throws NotFoundException, IOException
    {
        return new ArrayList<>();
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.insert()");
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.update()");
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.delete()");
    }

    @Override
    public void delete(ObjectVersionID id)
            throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.delete()");
    }

    @Override
    public boolean has(HierObjectID id)
            throws IOException
    {
        try
        {
            get(id);
            return true;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    @Override
    public boolean has(ObjectVersionID id)
            throws IOException
    {
        try
        {
            get(id);
            return true;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    @Override
    public boolean hasAny(ObjectVersionID id)
            throws IOException
    {
        try
        {
            get(id);
            return true;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException
    {
        return new ArrayList<>();
    }

    @Override
    public Iterable<ObjectVersionID> listVersions()
            throws IOException
    {
        return new ArrayList<>();
    }

    @Override
    public Iterable<Locatable> list(String XQuery)
            throws NotSupportedException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.list()");
    }

    @Override
    public void query(String XQuery, OutputStream os)
            throws NotSupportedException, IOException
    {
        throw new UnsupportedOperationException("todo implement NoopStore.query()");
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
    }

    @Override
    public boolean supports(Locatable test)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return true;
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
        return "OK";
    }

    @Override
    public String getName()
    {
        return "NoopStore";
    }

    @Override
    public boolean supportsTransactions()
    {
        return false;
    }

    @Override
    public void begin()
            throws TransactionException
    {
    }

    @Override
    public void commit()
            throws TransactionException
    {
    }

    @Override
    public void rollback()
            throws TransactionException
    {
    }
}
