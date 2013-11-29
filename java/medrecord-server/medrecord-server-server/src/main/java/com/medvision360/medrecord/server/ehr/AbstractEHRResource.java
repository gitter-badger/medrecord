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
import com.medvision360.medrecord.api.exceptions.DeletedException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import com.medvision360.medrecord.api.exceptions.InvalidEHRIDException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.DeletableEHR;
import org.openehr.rm.support.identification.HierObjectID;

public abstract class AbstractEHRResource extends AbstractServerResource
{
    protected org.openehr.rm.ehr.EHR getEHRModel()
            throws MissingParameterException, InvalidEHRIDException, NotFoundException, ParseException,
            InitializationException, IORecordException
    {
        String ignoreDeletedString = getQueryValue("ignoreDeleted");
        boolean ignoreDeleted = "true".equals(ignoreDeletedString);
        return getEHRModel(ignoreDeleted);
    }

    protected org.openehr.rm.ehr.EHR getEHRModel(boolean ignoreDeleted)
            throws MissingParameterException, InvalidEHRIDException, NotFoundException, ParseException,
            InitializationException, IORecordException
    {
        HierObjectID id = getEHRID();
        org.openehr.rm.ehr.EHR ehr;
        try
        {
            ehr = engine().getEHRStore().get(id);
        }
        catch (DeletedException e)
        {
            if (!ignoreDeleted)
            {
                throw e;
            }
            
            if (e.getDeleted() instanceof DeletableEHR)
            {
                ehr = (org.openehr.rm.ehr.EHR) e.getDeleted();
            }
            else
            {
                throw e;
            }
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
        return ehr;
    }
}
