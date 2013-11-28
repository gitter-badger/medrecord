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
import com.medvision360.medrecord.api.ehr.EHRListResource;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidLocatableTypeException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class EHRListServerResource
        extends AbstractEHRResource
        implements EHRListResource
{
    @Override
    public ID postEHR(Representation representation) throws RecordException
    {
        if (representation == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing body");
        }
        try
        {
            Locatable locatable = toLocatable(representation);
            if (!(locatable instanceof EHRStatus))
            {
                throw new InvalidLocatableTypeException(String.format(
                        "Expected a EHRStatus but got a %s", locatable.getClass().getSimpleName()));
            }
            EHRStatus ehrStatus = (EHRStatus) locatable;
            org.openehr.rm.ehr.EHR ehr = engine().createEHR(ehrStatus);
            ID result = new ID();
            result.setId(ehr.getEhrID().getValue());
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

    @Override
    public IDList listEHRs() throws RecordException
    {
        try
        {
            String excludeDeletedString = getQueryValue("excludeDeleted");
            Iterable<HierObjectID> list;
            if (excludeDeletedString != null)
            {
                boolean excludeDeleted = "true".equals(excludeDeletedString);
                list = engine().getEHRStore().list(excludeDeleted);
            }
            else
            {
                list = engine().getEHRStore().list();
            }
            IDList result = toIdList(list);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
