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

import com.medvision360.medrecord.api.ehr.EHRUndeleteResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.wslog.Events;
import org.openehr.rm.support.identification.HierObjectID;

public class EHRUndeleteServerResource
        extends AbstractEHRResource
        implements EHRUndeleteResource
{
    @Override
    public void undeleteEHR() throws RecordException
    {
        String id = null;
        try
        {
            HierObjectID hierObjectID = getEHRID();
            id = hierObjectID.getValue();
            try
            {
                engine().getEHRStore().undelete(hierObjectID);
                Events.append(
                        "UNDELETE",
                        id,
                        "EHR",
                        "undeleteEHR",
                        String.format(
                                "Restored EHR %s",
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
                    "undeleteEHRFailure",
                    String.format(
                            "Error restoring EHR %s: %s",
                            id,
                            e.getMessage()));
            throw e;
        }
    }
}
