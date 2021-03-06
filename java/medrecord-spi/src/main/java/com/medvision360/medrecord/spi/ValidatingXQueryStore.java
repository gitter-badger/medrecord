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
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.api.exceptions.IOValidationException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
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
