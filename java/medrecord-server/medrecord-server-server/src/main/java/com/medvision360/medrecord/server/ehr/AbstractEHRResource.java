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
    protected EHR toEHRResult(org.openehr.rm.ehr.EHR ehr)
    {
        EHR result = new EHR();
        if (ehr instanceof DeletableEHR)
        {
            result.setDeleted(((DeletableEHR) ehr).isDeleted());
        }
        if (ehr.getDirectory() != null)
        {
            result.setDirectoryId(ehr.getDirectory().getId().getValue());
        }
        result.setId(ehr.getEhrID().getValue());
        result.setStatusId(ehr.getEhrStatus().getId().getValue());
        result.setSystemId(ehr.getSystemID().getValue());
        return result;
    }

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
        HierObjectID id = getHierObjectIDAttribute();
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
