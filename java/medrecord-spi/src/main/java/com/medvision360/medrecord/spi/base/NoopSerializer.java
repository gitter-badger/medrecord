/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.base;

import java.io.OutputStream;

import com.medvision360.medrecord.spi.LocatableSerializer;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

public class NoopSerializer implements LocatableSerializer
{
    @Override
    public void serialize(Locatable locatable, OutputStream os)
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.serialize()");
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding)
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.serialize()");
    }

    @Override
    public String getMimeType()
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.getMimeType()");
    }

    @Override
    public String getFormat()
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.getFormat()");
    }

    @Override
    public boolean supports(Locatable test)
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.supports()");
    }

    @Override
    public boolean supports(Archetyped test)
    {
        throw new UnsupportedOperationException("todo implement NoopSerializer.supports()");
    }
}
