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
import com.medvision360.wslog.Events;
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
        String id = null;
        String className = "Locatable";
        String archetypeId = null;
        try
        {
            try
            {
                HierObjectID uid = getLocatableID();
                id = uid.getValue();
                LocatableSerializer serializer = engine().getLocatableSerializer("application/json", "json-pv");
                Locatable locatable = engine().getLocatableStore().get(uid);
                className = locatable.getClass().getSimpleName();
                archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();
                Representation result = new LocatableOutputRepresentation(serializer, locatable);
                Events.append(
                        "GET",
                        id,
                        className,
                        "getLocatable",
                        String.format(
                                "Retrieved %s %s of archetype %s",
                                className,
                                id,
                                archetypeId));
                return result;
            }
            catch (IOException e)
            {
                throw new IORecordException(e.getMessage(), e);
            }
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    className,
                    "getLocatableFailure",
                    String.format(
                            "Failed retrieving %s %s of archetype %s: %s",
                            className,
                            id,
                            archetypeId,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public void deleteLocatable() throws RecordException
    {
        String id = null;
        try
        {
            try
            {
                HierObjectID uid = getLocatableID();
                id = uid.getValue();
                engine().getLocatableStore().delete(uid);
                Events.append(
                        "DELETE",
                        id,
                        "Locatable",
                        "deleteLocatable",
                        String.format(
                                "Deleted locatable %s",
                                id));
            }
            catch (IOException e)
            {
                throw new IORecordException(e.getMessage(), e);
            }
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "Locatable",
                    "deleteLocatableFailure",
                    String.format(
                            "Failed deleting locatable %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public void putLocatable(Representation representation) throws RecordException
    {
        String id = null;
        String className = "Locatable";
        String archetypeId = null;
        try
        {
            if (representation == null)
            {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing body");
            }
            try
            {
                HierObjectID uid = getLocatableID();
                id = uid.getValue();
                if (!engine().getLocatableStore().has(uid))
                {
                    throw new NotFoundException(String.format("Locatable %s not found", uid));
                }
                
                LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), null);
                InputStream is = representation.getStream();
                Locatable locatable = parser.parse(is);
                className = locatable.getClass().getSimpleName();
                archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();
                engine().getLocatableStore().update(locatable);
                Events.append(
                        "UPDATE",
                        id,
                        className,
                        "putLocatable",
                        String.format(
                                "Updated %s %s of archetype %s",
                                className,
                                id,
                                archetypeId));
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
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "Locatable",
                    "putLocatableFailure",
                    String.format(
                            "Failed updating %s %s of archetype %s: %s",
                            className,
                            id,
                            archetypeId,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public void patchLocatable(Representation representation) throws RecordException
    {
        String id = null;
        String className = "Locatable";
        String archetypeId = null;
        try
        {
            if (representation == null)
            {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing body");
            }
            try
            {
                HierObjectID uid = getLocatableID();
                id = uid.getValue();
                LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), "json-pv");
    
                Locatable locatable = engine().getLocatableStore().get(uid);
                className = locatable.getClass().getSimpleName();
                archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();
    
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
                className = merged.getClass().getSimpleName();
                archetypeId = merged.getArchetypeDetails().getArchetypeId().getValue();
                engine().getLocatableStore().update(merged);
                Events.append(
                        "UPDATE",
                        id,
                        className,
                        "patchLocatable",
                        String.format(
                                "Modified %s %s of archetype %s",
                                className,
                                id,
                                archetypeId));
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
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "Locatable",
                    "patchLocatableFailure",
                    String.format(
                            "Failed modifying %s %s of archetype %s: %s",
                            className,
                            id,
                            archetypeId,
                            e.getMessage()));
            throw e;
        }
    }

}
