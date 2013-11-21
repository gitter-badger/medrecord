package com.medvision360.medrecord.server.query;

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.query.QueryEHRResource;
import com.medvision360.medrecord.server.AbstractServerResource;

public class QueryEHRServerResource
        extends AbstractServerResource
        implements QueryEHRResource
{
    @Override
    public IDList ehrQuery() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
