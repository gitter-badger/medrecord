package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.exceptions.IOValidationException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;

import static com.google.common.base.Preconditions.checkNotNull;

public class ValidatingXQueryStore extends ValidatingLocatableStore implements XQueryStore
{
    private XQueryStore m_delegate;

    public ValidatingXQueryStore(String name, LocatableSelector locatableSelector,
            XQueryStore delegate, LocatableValidator validator)
    {
        super(name, locatableSelector, delegate, validator);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
    }

    public ValidatingXQueryStore(String name, XQueryStore delegate, LocatableValidator validator)
    {
        super(name, delegate, validator);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
    }

    @Override
    public Iterable<Locatable> query(String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        Iterable<Locatable> result = m_delegate.query(XQuery);
        validateOnRetrieve(result);
        return result;
    }

    @Override
    public Iterable<Locatable> query(EHR EHR, String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(XQuery, "XQuery cannot be null");
        Iterable<Locatable> result = m_delegate.query(EHR, XQuery);
        validateOnRetrieve(result);
        return result;
    }

    @Override
    public void query(String XQuery, OutputStream os) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        m_delegate.query(XQuery, os);
    }

    protected void validateOnRetrieve(Iterable<Locatable> result) throws IOValidationException
    {
        if (m_validateOnRetrieve)
        {
            for (Locatable locatable : result)
            {
                validateOnRetrieve(locatable);
            }
        }
    }
}
