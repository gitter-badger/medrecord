package com.medvision360.medrecord.server.ehr;

import java.io.IOException;

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.ehr.EHRResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.openehr.rm.support.identification.HierObjectID;

public class EHRServerResource
        extends AbstractEHRResource implements EHRResource
{
    @Override
    public EHR getEHR()
            throws RecordException
    {
        org.openehr.rm.ehr.EHR ehr = getEHRModel();
        EHR result = toEHRResult(ehr);
        return result;
    }

    @Override
    public void deleteEHR() throws RecordException
    {
        HierObjectID id = getHierObjectIDAttribute();
        try
        {
            engine().getEHRStore().delete(id);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

}
