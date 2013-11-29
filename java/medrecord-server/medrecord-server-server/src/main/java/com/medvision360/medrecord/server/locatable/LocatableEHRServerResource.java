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

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.locatable.LocatableEHRResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.openehr.rm.support.identification.HierObjectID;

public class LocatableEHRServerResource
        extends AbstractServerResource
        implements LocatableEHRResource
{
    @Override
    public EHR getEHRForLocatable()
            throws RecordException
    {
        HierObjectID uid = getLocatableID();

        try
        {
            org.openehr.rm.ehr.EHR ehr = engine().getEHRForLocatable(uid);
            EHR result = toEHRResult(ehr);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
