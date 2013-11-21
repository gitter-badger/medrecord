package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransformingXQueryStore extends TransformingLocatableStore implements XQueryStore
{
    private XQueryStore m_delegate;

    public TransformingXQueryStore(String name, LocatableSelector locatableSelector,
            XQueryStore delegate, LocatableTransformer transformer)
    {
        super(name, locatableSelector, delegate, transformer);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
    }

    public TransformingXQueryStore(String name, XQueryStore delegate, LocatableTransformer transformer)
    {
        super(name, delegate, transformer);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
    }

    @Override
    public Iterable<Locatable> query(String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        Iterable<Locatable> result = m_delegate.query(XQuery);
        return result;
    }

    @Override
    public Iterable<Locatable> query(EHR EHR, String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(XQuery, "XQuery cannot be null");
        Iterable<Locatable> result = m_delegate.query(EHR, XQuery);
        return result;
    }

    @Override
    public void query(String XQuery, OutputStream os) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        m_delegate.query(XQuery, os);
    }
}
