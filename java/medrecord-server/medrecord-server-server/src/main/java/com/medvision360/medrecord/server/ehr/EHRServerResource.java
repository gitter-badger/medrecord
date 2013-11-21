package com.medvision360.medrecord.server.ehr;

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.ehr.EHRResource;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.server.AbstractServerResource;

public class EHRServerResource
        extends AbstractServerResource
        implements EHRResource
{
    @Override
    public EHR getEHR()
            throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }

    @Override
    public void deleteEHR() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
