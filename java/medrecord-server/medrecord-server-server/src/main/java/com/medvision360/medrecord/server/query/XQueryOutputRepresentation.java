package com.medvision360.medrecord.server.query;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.XQueryStore;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

public class XQueryOutputRepresentation extends OutputRepresentation
{
    private XQueryStore m_store;
    private String m_query;
    
    public XQueryOutputRepresentation(MediaType mediaType, XQueryStore store, String query)
    {
        super(mediaType);
        m_store = store;
        m_query = query;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException
    {
        try
        {
            m_store.query(m_query, outputStream);
        }
        catch (NotSupportedException e)
        {
            throw new IOException(e);
        }
    }
}
