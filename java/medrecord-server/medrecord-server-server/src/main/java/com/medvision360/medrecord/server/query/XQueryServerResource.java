package com.medvision360.medrecord.server.query;

import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.query.XQueryResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.restlet.representation.Representation;

public class XQueryServerResource
        extends AbstractServerResource
        implements XQueryResource
{
    @Override
    public Representation xQuery() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
