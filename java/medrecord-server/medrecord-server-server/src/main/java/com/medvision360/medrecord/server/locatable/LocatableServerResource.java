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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;

import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.locatable.LocatableResource;
import com.medvision360.medrecord.pv.PVMapMaker;
import com.medvision360.medrecord.pv.PVMapSerializer;
import com.medvision360.medrecord.pv.PVReader;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class LocatableServerResource
        extends AbstractServerResource
        implements LocatableResource
{
    @Override
    public Representation getLocatable() throws RecordException
    {
        try
        {
            HierObjectID uid = getLocatableID();
            LocatableSerializer serializer = engine().getLocatableSerializer("application/json", "json-pv");
            Locatable locatable = engine().getLocatableStore().get(uid);
            Representation result = new LocatableOutputRepresentation(serializer, locatable);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteLocatable() throws RecordException
    {
        try
        {
            HierObjectID uid = getLocatableID();
            engine().getLocatableStore().delete(uid);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public void putLocatable(Representation representation) throws RecordException
    {
        if (representation == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing body");
        }
        try
        {
            HierObjectID uid = getLocatableID();
            if (!engine().getLocatableStore().has(uid))
            {
                throw new NotFoundException(String.format("Locatable %s not found", uid));
            }
            
            LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), null);
            InputStream is = representation.getStream();
            Locatable locatable = parser.parse(is);
            engine().getLocatableStore().insert(locatable);
        }
        catch (ParseException e)
        {
            throw new ClientParseException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public void patchLocatable(Representation representation) throws RecordException
    {
        if (representation == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing body");
        }
        try
        {
            HierObjectID uid = getLocatableID();
            LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), "json-pv");

            Locatable locatable = engine().getLocatableStore().get(uid);

            PVMapMaker mapMaker = new PVMapMaker();
            mapMaker.serialize(locatable, null);
            if (!engine().getLocatableStore().has(uid))
            {
                throw new NotFoundException(String.format("Locatable %s not found", uid));
            }
            SortedMap<String, Object> pvMap = mapMaker.getMap();
            
            InputStream is = representation.getStream();
            PVReader reader = new PVReader();
            SortedMap<String, String> patch = reader.toMap(is);
            for (String path : patch.keySet())
            {
                if (path.matches("(?:\\[[^\\]]+\\])?/uid/value"))
                {
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                            "Cannot replace /uid/value");
                }
            }
            pvMap.putAll(patch);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PVMapSerializer serializer = new PVMapSerializer();
            serializer.serialize(pvMap, os, "UTF-8");
            byte[] mergedBuf = os.toByteArray();
            is = new ByteArrayInputStream(mergedBuf);
            
            Locatable merged = parser.parse(is);
            engine().getLocatableStore().update(merged);
        }
        catch (ParseException e)
        {
            throw new ClientParseException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

}
