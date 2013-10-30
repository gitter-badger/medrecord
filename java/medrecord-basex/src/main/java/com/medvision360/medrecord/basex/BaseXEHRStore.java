package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.medvision360.medrecord.spi.EHRParser;
import com.medvision360.medrecord.spi.EHRSerializer;
import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.basex.core.Context;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXEHRStore extends AbstractBaseXStore implements EHRStore
{
    private HierObjectID m_systemID;

    protected EHRParser m_parser;
    protected EHRSerializer m_serializer;

    public BaseXEHRStore(Context ctx, EHRParser parser, EHRSerializer serializer, String path,
            HierObjectID systemID)
    {
        super(ctx, systemID.getValue(), path);
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
        m_systemID = checkNotNull(systemID, "systemID cannot be null");
    }

    @Override
    public HierObjectID getSystemID()
    {
        return m_systemID;
    }

    @Override
    public EHR get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        EHR result = get(path, id);
        return result;
    }

    @Override
    public EHR insert(EHR EHR) throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        throw new UnsupportedOperationException("todo implement BaseXEHRStore.insert()");
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXEHRStore.delete()");
    }

    @Override
    public void undelete(HierObjectID id) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXEHRStore.undelete()");
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXEHRStore.has()");
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXEHRStore.list()");
    }

    protected EHR get(String path, HierObjectID argument) throws IOException, NotFoundException, ParseException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        get(path, argument, os);
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        return m_parser.parse(is);
    }
}
