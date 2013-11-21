package com.medvision360.medrecord.server.locatable;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import com.medvision360.medrecord.api.locatable.LocatableListResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.LocatableParser;
import org.openehr.rm.common.archetyped.Locatable;
import org.restlet.representation.Representation;

public class LocatableListServerResource
        extends AbstractServerResource
        implements LocatableListResource
{
    @Override
    public ID postLocatable(Representation representation) throws RecordException
    {
        try
        {
            LocatableParser parser = engine().getLocatableParser(representation.getMediaType().getName(), null);
            InputStream is = representation.getStream();
            Locatable locatable = parser.parse(is);
            Locatable inserted = engine().getLocatableStore().insert(locatable);
            ID id = new ID();
            id.setId(inserted.getUid().getValue());
            return id;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public IDList listLocatables() throws RecordException
    {
        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
