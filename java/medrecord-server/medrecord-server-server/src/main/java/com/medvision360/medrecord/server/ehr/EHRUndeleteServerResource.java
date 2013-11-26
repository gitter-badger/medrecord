package com.medvision360.medrecord.server.ehr;

import java.io.IOException;

import com.medvision360.medrecord.api.ehr.EHRUndeleteResource;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.DeletableEHR;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

public class EHRUndeleteServerResource
        extends AbstractEHRResource
        implements EHRUndeleteResource
{
    @Override
    public void undeleteEHR() throws RecordException
    {
        HierObjectID id = getHierObjectIDAttribute();
        try
        {
            engine().getEHRStore().undelete(id);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
