package com.medvision360.medrecord.server.query;

import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.query.XQueryLocatableResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.restlet.representation.Representation;

public class XQueryLocatableServerResource
        extends AbstractServerResource
        implements XQueryLocatableResource
{
    @Override
    public Representation locatableXQuery() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
