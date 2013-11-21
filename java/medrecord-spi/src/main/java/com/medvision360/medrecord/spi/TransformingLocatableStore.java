package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.api.exceptions.StatusException;
import com.medvision360.medrecord.api.exceptions.TransformException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransformingLocatableStore extends AbstractLocatableStore implements LocatableStore
{
    protected LocatableStore m_delegate;
    protected LocatableTransformer m_transformer;
    
    public TransformingLocatableStore(String name, LocatableSelector locatableSelector,
            LocatableStore delegate, LocatableTransformer transformer)
    {
        super(name, locatableSelector);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
        m_transformer = checkNotNull(transformer, "transformer cannot be null");
    }

    public TransformingLocatableStore(String name, LocatableStore delegate, LocatableTransformer transformer)
    {
        super(name);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
        m_transformer = checkNotNull(transformer, "transformer cannot be null");
    }

    @Override
    public boolean supports(Locatable test)
    {
        return super.supports(test) && m_delegate.supports(test);
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return super.supports(test) && m_delegate.supports(test);
    }

    @Override
    public Locatable get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        return m_delegate.get(id);
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException
    {
        checkNotNull(locatable, "locatable cannot be null");
        try
        {
            transform(locatable);
        }
        catch (TransformException e)
        {
            throw new IOException(e);
        }
        return m_delegate.insert(locatable);
    }

    @Override
    public Locatable insert(EHR EHR, Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(locatable, "locatable cannot be null");
        try
        {
            transform(locatable);
        }
        catch (TransformException e)
        {
            throw new IOException(e);
        }
        return m_delegate.insert(EHR, locatable);
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException, SerializeException, ValidationException
    {
        checkNotNull(locatable, "locatable cannot be null");
        try
        {
            transform(locatable);
        }
        catch (TransformException e)
        {
            throw new IOException(e);
        }
        return m_delegate.update(locatable);
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        m_delegate.delete(id);
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        return m_delegate.has(id);
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        return m_delegate.list();
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR) throws IOException, NotFoundException
    {
        return m_delegate.list(EHR);
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR, String rmEntity) throws IOException, NotFoundException
    {
        return m_delegate.list(EHR, rmEntity);
    }

    @Override
    public void clear() throws IOException
    {
        m_delegate.clear();
    }

    @Override
    public void verifyStatus() throws StatusException
    {
        m_delegate.verifyStatus();
    }

    @Override
    public String reportStatus() throws StatusException
    {
        return m_delegate.reportStatus();
    }

    protected void transform(Locatable locatable) throws TransformException
    {
        m_transformer.transform(locatable);
    }
}
