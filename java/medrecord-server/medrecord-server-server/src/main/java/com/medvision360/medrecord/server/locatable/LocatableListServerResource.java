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
package com.medvision360.medrecord.server.locatable;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.locatable.LocatableListResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.LocatableParser;
import org.openehr.rm.common.archetyped.Locatable;
import org.restlet.representation.Representation;

public class LocatableListServerResource
        extends AbstractServerResource
        implements LocatableListResource
{
    @Override
    public ID postLocatable(Representation representation) throws RecordException
    {
        try
        {
            LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), null);
            InputStream is = representation.getStream();
            Locatable locatable = parser.parse(is);
            Locatable inserted = engine().getLocatableStore().insert(locatable);
            ID id = new ID();
            id.setId(inserted.getUid().getValue());
            return id;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public IDList listLocatables() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
