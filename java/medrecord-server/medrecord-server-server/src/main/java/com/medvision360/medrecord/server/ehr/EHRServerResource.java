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

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.ehr.EHRResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.wslog.Events;
import org.openehr.rm.support.identification.HierObjectID;

public class EHRServerResource
        extends AbstractEHRResource implements EHRResource
{
    @Override
    public EHR getEHR()
            throws RecordException
    {
        String id = null;
        try
        {
            org.openehr.rm.ehr.EHR ehr = getEHRModel();
            EHR result = toEHRResult(ehr);
            id = result.getId();
            Events.append(
                    "GET",
                    id,
                    "EHR",
                    "getEHR",
                    String.format(
                            "Retrieved EHR %s",
                            id));
            return result;
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    id,
                    "EHR",
                    "getEHRFailure",
                    String.format(
                            "Failed to get EHR %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

    @Override
    public void deleteEHR() throws RecordException
    {
        String id = null;
        try
        {
            HierObjectID hierObjectID = getEHRID();
            id = hierObjectID.getValue();
            try
            {
                engine().getEHRStore().delete(hierObjectID);
                Events.append(
                        "DELETE",
                        id,
                        "EHR",
                        "deleteEHR",
                        String.format(
                                "Deleted EHR %s",
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
                    "EHR",
                    "deleteEHRFailure",
                    String.format(
                            "Failed to delete EHR %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }

}
