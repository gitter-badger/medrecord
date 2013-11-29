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
package com.medvision360.medrecord.server.archetype;

import java.io.IOException;

import com.medvision360.medrecord.api.archetype.ArchetypeResource;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.wslog.Events;
import org.openehr.rm.support.identification.ArchetypeID;

public class ArchetypeServerResource
        extends AbstractServerResource
        implements ArchetypeResource
{
    @Override
    public ArchetypeResult getArchetype() throws RecordException
    {
        String id = null;
        try
        {
            WrappedArchetype wrappedArchetype = getWrappedArchetype();
            String adl = wrappedArchetype.getAsString();
            id = wrappedArchetype.getArchetype().getArchetypeId().getValue();
            ArchetypeResult result = new ArchetypeResult();
            result.setArchetypeId(id);
            result.setAdl(adl);
            Events.append(
                    "GET",
                    id,
                    "ARCHETYPE",
                    "getArchetype",
                    String.format(
                            "Retrieved archetype %s",
                            id));
            return result;
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "ARCHETYPE",
                    "getArchetypeFailure",
                    String.format(
                            "Error retrieving archetype %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public String getArchetypeAsText() throws RecordException
    {
        String id = null;
        try
        {
            ArchetypeID archetypeID = getArchetypeIDAttribute();
            id = archetypeID.getValue();
            WrappedArchetype wrappedArchetype = getWrappedArchetype();
            Events.append(
                    "GET",
                    id,
                    "ARCHETYPE",
                    "getArchetypeAsText",
                    String.format(
                            "Retrieved archetype %s",
                            id));
            return wrappedArchetype.getAsString();
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "ARCHETYPE",
                    "getArchetypeAsTextFailure",
                    String.format(
                            "Error retrieving archetype %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public void deleteArchetype() throws RecordException
    {
        String id = null;
        try
        {
            try
            {
                ArchetypeID archetypeID = getArchetypeIDAttribute();
                id = archetypeID.getValue();
                engine().getArchetypeStore().delete(archetypeID);
                Events.append(
                        "DELETE",
                        id,
                        "ARCHETYPE",
                        "getArchetypeAsText",
                        String.format(
                                "Deleted archetype %s",
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
                    "ARCHETYPE",
                    "getArchetypeAsTextFailure",
                    String.format(
                            "Error deleting archetype %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

    private WrappedArchetype getWrappedArchetype() throws RecordException
    {
        try
        {
            ArchetypeID archetypeID = getArchetypeIDAttribute();
    
            MedRecordEngine engine = engine();
            return engine.getArchetypeStore().get(archetypeID);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    private ArchetypeID getArchetypeIDAttribute() throws MissingParameterException, InvalidArchetypeIDException
    {
        String id = getRequiredAttributeValue("id");
        try
        {
            return new ArchetypeID(id);
        }
        catch (IllegalArgumentException e)
        {
            throw new InvalidArchetypeIDException(id);
        }
    }
}
