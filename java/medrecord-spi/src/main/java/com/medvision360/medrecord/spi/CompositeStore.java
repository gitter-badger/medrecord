/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeStore implements XQueryStore, CompositeService<LocatableStore>
{
    private List<LocatableStore> m_delegates = new LinkedList<>();
    private String m_name;

    public CompositeStore(String name)
    {
        m_name = checkNotNull(name, "name cannot be null");
    }

    public void addDelegate(LocatableStore delegate)
    {
        m_delegates.add(checkNotNull(delegate, "delegate cannot be null"));
    }

    @Override
    public Locatable get(HierObjectID id)
            throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : m_delegates)
        {
            try
            {
                return delegate.get(id);
            }
            catch (NotFoundException e)
            {
                if (nfe == null)
                {
                    nfe = e;
                }
            }
            catch (IOException e)
            {
                if (ioe == null)
                {
                    ioe = e;
                }
            }
        }
        if (ioe != null)
        {
            throw ioe;
        }
        if (nfe != null)
        {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Locatable get(ObjectVersionID id)
            throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : m_delegates)
        {
            try
            {
                return delegate.get(id);
            }
            catch (NotFoundException e)
            {
                if (nfe == null)
                {
                    nfe = e;
                }
            }
            catch (IOException e)
            {
                if (ioe == null)
                {
                    ioe = e;
                }
            }
        }
        if (ioe != null)
        {
            throw ioe;
        }
        if (nfe != null)
        {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id)
            throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : m_delegates)
        {
            try
            {
                return delegate.getVersions(id);
            }
            catch (NotFoundException e)
            {
                if (nfe == null)
                {
                    nfe = e;
                }
            }
            catch (IOException e)
            {
                if (ioe == null)
                {
                    ioe = e;
                }
            }
        }
        if (ioe != null)
        {
            throw ioe;
        }
        if (nfe != null)
        {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(locatable, "locatable cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.supports(locatable))
            {
                return delegate.insert(locatable);
            }
        }
        throw new NotSupportedException(String.format("No delegate store supports the locatable %s", locatable));
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException, SerializeException
    {
        checkNotNull(locatable, "locatable cannot be null");
        UIDBasedID uidBasedID = locatable.getUid();
        HierObjectID hierObjectID = uidBasedID instanceof HierObjectID ? (HierObjectID) uidBasedID : null;
        ObjectVersionID objectVersionID = uidBasedID instanceof ObjectVersionID ? (ObjectVersionID) uidBasedID : null;
        if (hierObjectID == null && objectVersionID == null)
        {
            throw new NotSupportedException(
                    String.format("Locable UID of locatable %s has to be a HierObjectID or ObjectVersionID, was %s",
                            locatable, uidBasedID.getClass().getSimpleName()));
        }
        boolean haveHierObjectID = hierObjectID != null;

        for (LocatableStore delegate : m_delegates)
        {
            boolean found = haveHierObjectID ? delegate.has(hierObjectID) : delegate.hasAny(objectVersionID);
            if (found)
            {
                return delegate.update(locatable);
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", locatable));
    }

    @Override
    public boolean has(HierObjectID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.has(id))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean has(ObjectVersionID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.has(id))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAny(ObjectVersionID id)
            throws IOException
    {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.hasAny(id))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.has(id))
            {
                delegate.delete(id);
                return;
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public void delete(ObjectVersionID id)
            throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.has(id))
            {
                delegate.delete(id);
                return;
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException
    {
        List<Iterable<HierObjectID>> all = new LinkedList<>();
        for (LocatableStore delegate : m_delegates)
        {
            all.add(delegate.list());
        }
        Iterable<HierObjectID> result = Iterables.concat(all);
        return result;
    }

    @Override
    public Iterable<ObjectVersionID> listVersions()
            throws IOException
    {
        List<Iterable<ObjectVersionID>> all = new LinkedList<>();
        for (LocatableStore delegate : m_delegates)
        {
            all.add(delegate.listVersions());
        }
        Iterable<ObjectVersionID> result = Iterables.concat(all);
        return result;
    }

    @Override
    public Iterable<Locatable> list(String XQuery)
            throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        List<Iterable<Locatable>> all = new LinkedList<>();
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate instanceof XQueryStore)
            {
                all.add(((XQueryStore) delegate).list(XQuery));
            }
        }
        Iterable<Locatable> result = Iterables.concat(all);
        return result;
    }

    @Override
    public void query(String XQuery, OutputStream os)
            throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate instanceof XQueryStore)
            {
                ((XQueryStore) delegate).query(XQuery, os);
            }
        }
    }

    @Override
    public void initialize()
            throws IOException
    {
        for (LocatableStore delegate : m_delegates)
        {
            delegate.initialize();
        }
    }

    @Override
    public void clear()
            throws IOException
    {
        for (LocatableStore delegate : m_delegates)
        {
            delegate.clear();
        }
    }

    @Override
    public boolean supports(Locatable test)
    {
        checkNotNull(test, "locatable cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.supports(test))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        checkNotNull(test, "archetyped cannot be null");
        for (LocatableStore delegate : m_delegates)
        {
            if (delegate.supports(test))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void verifyStatus()
            throws StatusException
    {
        for (LocatableStore delegate : m_delegates)
        {
            delegate.verifyStatus();
        }
    }

    @Override
    public String reportStatus()
            throws StatusException
    {
        StringBuilder b = new StringBuilder();
        for (LocatableStore delegate : m_delegates)
        {
            b.append(delegate.getName());
            b.append(": ");
            b.append(delegate.reportStatus());
            b.append("\n");
        }
        return b.toString();
    }

    @Override
    public String getName()
    {
        return this.m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        for (LocatableStore delegate : m_delegates)
        {
            if (!delegate.supportsTransactions())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void begin()
            throws TransactionException
    {
        for (LocatableStore delegate : m_delegates)
        {
            delegate.begin();
        }
    }

    @Override
    public void commit()
            throws TransactionException
    {
        // todo we _should_ have two-phase commit for this, but, well, we don't
        for (LocatableStore delegate : m_delegates)
        {
            delegate.commit();
        }
    }

    @Override
    public void rollback()
            throws TransactionException
    {
        for (LocatableStore delegate : m_delegates)
        {
            delegate.rollback();
        }
    }
}
