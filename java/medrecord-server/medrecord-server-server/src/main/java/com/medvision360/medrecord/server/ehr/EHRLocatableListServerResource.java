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
package com.medvision360.medrecord.server.ehr;

import java.io.IOException;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.ehr.EHRLocatableListResource;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.wslog.Events;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class EHRLocatableListServerResource
        extends AbstractEHRResource
        implements EHRLocatableListResource
{
    @Override
    public ID postLocatable(Representation representation) throws RecordException
    {
        String id = null;
        String ehrId = null;
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
                boolean ignoreDeleted = false; // can't update a deleted EHR
                EHR ehr = getEHRModel(ignoreDeleted);
                ehrId = ehr.getEhrID().getValue();
                Locatable locatable = toLocatable(representation);
                id = locatable.getUid() == null ? null : locatable.getUid().getValue();
                className = locatable.getClass().getSimpleName();
                archetypeId = locatable.getArchetypeDetails().getArchetypeId().getValue();
                Locatable inserted = engine().getLocatableStore().insert(ehr, locatable);
                id = inserted.getUid().getValue();
                ID result = new ID();
                result.setId(id);
                Events.append(
                        "INSERT",
                        id,
                        className,
                        "postLocatable",
                        String.format(
                                "Inserted %s %s of archetype %s in EHR %s",
                                className, id, archetypeId, ehrId));
                return result;
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
                    className,
                    "postLocatableFailure",
                    String.format(
                            "Failed to insert %s %s of archetype %s in EHR %s: %s",
                            className, id, archetypeId, ehrId, e.getMessage()));
            throw e;
        }
    }

    @Override
    public IDList listLocatables() throws RecordException
    {
        String ehrId = null;
        try
        {
            try
            {
                EHR ehr = getEHRModel();
                ehrId = ehr.getEhrID().getValue();
                Iterable<HierObjectID> list = engine().getLocatableStore().list(ehr);
                IDList result = toIdList(list);
                Events.append(
                        "LIST",
                        "ehr="+ehrId,
                        "Locatable",
                        "listLocatables",
                        String.format(
                                "Listed locatables for EHR %s",
                                ehrId));
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
                    "all",
                    "Locatable",
                    "listLocatablesFailure",
                    String.format(
                            "Failed listing locatables for EHR %s:",
                            ehrId,
                            e.getMessage()));
            throw e;
        }
    }
}
