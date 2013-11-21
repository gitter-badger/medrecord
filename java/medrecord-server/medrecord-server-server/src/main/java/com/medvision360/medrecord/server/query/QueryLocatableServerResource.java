package com.medvision360.medrecord.server.query;

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.query.QueryLocatableResource;
import com.medvision360.medrecord.server.AbstractServerResource;

public class QueryLocatableServerResource
        extends AbstractServerResource
        implements QueryLocatableResource
{
    @Override
    public IDList locatableQuery() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
