package com.medvision360.medrecord.server.ehr;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.ehr.EHRListResource;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.restlet.representation.Representation;

public class EHRListServerResource
        extends AbstractServerResource
        implements EHRListResource
{
    @Override
    public ID postEHR(Representation representation) throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }

    @Override
    public IDList listEHRs() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
