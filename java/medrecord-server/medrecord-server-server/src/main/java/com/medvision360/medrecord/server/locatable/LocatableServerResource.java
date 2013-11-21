package com.medvision360.medrecord.server.locatable;

import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.locatable.LocatableResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.restlet.representation.Representation;

public class LocatableServerResource
        extends AbstractServerResource
        implements LocatableResource
{
    @Override
    public Representation getLocatable() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }

    @Override
    public void deleteLocatable() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }

    @Override
    public void putLocatable(Representation representation) throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }

    @Override
    public void patchLocatable(Representation representation) throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
