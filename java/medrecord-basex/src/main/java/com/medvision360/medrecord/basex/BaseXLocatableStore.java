package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXLocatableStore extends AbstractBaseXStore implements XQueryStore
{
    // DOCUMENT STRUCTURE
    // ------------------
    //   /{m_path}/medrecord_locatables
    //      <medrecord_locatables>
    //         <medrecord_locatable .../>
    //      </medrecord_locatables>
    //   /{m_path}/locatables/hPath({HierObjectID})
    //      <{rmTypeName} archetype_id="...." .../>

    protected LocatableParser m_parser;
    protected LocatableSerializer m_serializer;

    public BaseXLocatableStore(Context ctx, LocatableParser parser, LocatableSerializer serializer,
            LocatableSelector locatableSelector, String name, String path)
    {
        super(ctx, locatableSelector, name, path);
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
    }

    @Override
    public boolean supports(Locatable test)
    {
        return super.supports(test) && m_parser.supports(test) && m_serializer.supports(test);
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return super.supports(test) && m_parser.supports(test) && m_serializer.supports(test);
    }

    @Override
    public Locatable get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        Locatable result = get(path, id);
        return result;
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(locatable, "locatable cannot be null");
        String path = fullPath(locatable);
        if (has(path))
        {
            throw duplicate(locatable);
        }
        Locatable result = replace(locatable, path);
        return result;
    }

    @Override
    public Locatable insert(EHR EHR, Locatable locatable)
    throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(locatable, "locatable cannot be null");
        String path = fullPath(locatable);
        if (has(path))
        {
            throw duplicate(locatable);
        }

        Locatable result = replace(locatable, path);

        String linkPath = fullPath(EHR, locatable);
        if (!has(linkPath))
        {
            replace(linkPath);
        }

        return result;
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException, SerializeException
    {
        checkNotNull(locatable, "locatable cannot be null");
        String path = fullPath(locatable);
        if (!has(path))
        {
            throw notFound(locatable);
        }
        Locatable result = replace(locatable, path);
        return result;
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        if (!has(path))
        {
            throw notFound(id);
        }

        delete(path);
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        return has(path);
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        String path = fullPath("locatables");
        return list(path);
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR) throws IOException, NotFoundException
    {
        checkNotNull(EHR, "EHR cannot be null");
        // this works because ListDocs looks for all leaf nodes under a certain path
        String path = fullPath(EHR);
        Iterable<HierObjectID> result = list(path);
        if (Iterables.size(result) == 0)
        {
            throw new NotFoundException(String.format("EHR %s not found", EHR.getEhrID()));
        }
        return result;
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR, String rmEntity) throws IOException, NotFoundException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(rmEntity, "rmEntity cannot be null");
        String path = fullPath(EHR) + "/" + rmEntity;
        Iterable<HierObjectID> result = list(path);
        if (Iterables.size(result) == 0)
        {
            // for throwing NotFoundException if the EHR does not exist
            list(EHR);
        }
        return result;
    }

    @Override
    public Iterable<Locatable> query(String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.query()");
    }

    @Override
    public Iterable<Locatable> query(EHR EHR, String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(XQuery, "XQuery cannot be null");
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.query()");
    }

    @Override
    public void query(String XQuery, OutputStream os) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        xquery(XQuery, os);
    }

    ///
    /// Helpers
    ///

    protected Locatable get(String path, Object argument) throws IOException, NotFoundException, ParseException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        get(path, argument, os); // calls initialize()
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        return m_parser.parse(is);
    }

    protected Locatable replace(Locatable locatable, String path) throws IOException, SerializeException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(locatable, os);
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        replace(path, is); // calls initialize()

        return locatable;
    }

    protected void xquery(String XQuery, OutputStream os) throws IOException
    {
        initialize();
        org.basex.core.cmd.XQuery cmd = new XQuery(XQuery);
        cmd.execute(m_ctx, os);
    }

}
