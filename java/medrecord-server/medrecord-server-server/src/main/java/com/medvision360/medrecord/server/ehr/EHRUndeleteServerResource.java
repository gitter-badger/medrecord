package com.medvision360.medrecord.server.ehr;

import com.medvision360.medrecord.api.ehr.EHRUndeleteResource;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.server.AbstractServerResource;

public class EHRUndeleteServerResource
        extends AbstractServerResource
        implements EHRUndeleteResource
{
    @Override
    public void undeleteEHR() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
