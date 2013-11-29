package com.medvision360.medrecord.server.locatable;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.spi.LocatableSerializer;
import org.openehr.rm.common.archetyped.Locatable;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

class LocatableOutputRepresentation extends OutputRepresentation
{
    private LocatableSerializer m_serializer;
    private Locatable m_locatable;

    public LocatableOutputRepresentation(LocatableSerializer serializer, Locatable locatable)
    {
        super(MediaType.APPLICATION_JSON);
        m_serializer = serializer;
        m_locatable = locatable;
    }

    @Override
    public void write(OutputStream os) throws IOException
    {
        try
        {
            m_serializer.serialize(m_locatable, os);
        }
        catch (SerializeException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
}
