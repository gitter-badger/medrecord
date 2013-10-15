package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Optimize;
import org.basex.core.cmd.Retrieve;
import org.basex.core.cmd.Store;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXLocatableStore implements LocatableStore
{
    // one store = one database
    // name is used as database name
    // note basex does not have hierarchical collections
    // document structure:
    //   /{m_path}/version
    //      <version>1</version>
    //   /{m_path}/medrecord_locatables
    //      <medrecord_locatables>
    //         <medrecord_locatable .../>
    //      </medrecord_locatables>
    //   /{m_path}/medrecord_locatable_versions
    //      <medrecord_locatable_versions>
    //         <medrecord_locatable_version .../>
    //      </medrecord_locatable_versions>
    //   /{m_path}/ocatables/hpath({HierObjectID})
    //      <{rmTypeName} archetype_id="...." .../>
    //   /{m_path}/locatable_versions/{ObjectVersionID}
    //      <{rmTypeName} archetype_id="...." .../>
    // faithfully uses Command objects to lock around BaseX, so _should_ be thread-safe
    
    
    private final static String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9\\._-]+$";
    private final static String PATH_REGEX = "^[a-zA-Z0-9\\._-]+(?:/[a-zA-Z0-9\\._-]+)*$";
    private final static String VERSION_PATH = "/version";
    private final static String VERSION_INITIAL = "<version>1</version>";
    
    private Context m_ctx;
    private LocatableParser m_parser;
    private LocatableSerializer m_serializer;
    private String m_name;
    private String m_path;
    private boolean m_initialized = false;

    public BaseXLocatableStore(Context ctx, LocatableParser parser, LocatableSerializer serializer,
            String name, String path)
    {
        m_ctx = checkNotNull(ctx, "ctx cannot be null");
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
        m_name = checkNotNull(name, "name cannot be null");
        checkArgument(name.matches(NAME_REGEX), "name has to match regex %s", NAME_REGEX);
        setPath(name, path);
    }

    private void setPath(String name, String path)
    {
        m_path = checkNotNull(path, "path cannot be null");
        checkArgument(name.matches(PATH_REGEX), "path has to match regex %s", PATH_REGEX);
        if (!m_path.startsWith("/")) {
            m_path = "/" + m_path;
        }
        if (!m_path.endsWith("/")) {
            m_path = m_path + "/";
        }
        if ("//".equals(m_path)) {
            m_path = "/";
        }
    }

    @Override
    public Locatable get(HierObjectID id) throws NotFoundException, IOException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Retrieve cmd = new Retrieve(path);
        
        cmd.execute(m_ctx, os);
        
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        Locatable result = m_parser.parse(is);
        
        return result;
    }

    @Override
    public Locatable get(ObjectVersionID id) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.get()");
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.getVersions()");
    }

    @Override
    public Locatable insert(Locatable locatable) throws DuplicateException, NotSupportedException, IOException
    {
        checkNotNull(locatable, "locatable cannot be null");
        String path = fullPath(locatable);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(locatable, os);
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        Add cmd = new Add(path);
        cmd.setInput(is);

        try
        {
            cmd.execute(m_ctx);
        }
        finally
        {
            optimize();
        }
        
        return locatable;
    }

    @Override
    public Locatable update(Locatable locatable) throws NotSupportedException, NotFoundException, IOException
    {
        try
        {
            throw new UnsupportedOperationException("todo implement");
        }
        finally
        {
            optimize();
        }
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        try
        {
            throw new UnsupportedOperationException("todo implement");
        }
        finally
        {
            optimize();
        }
    }

    @Override
    public void delete(ObjectVersionID id) throws NotFoundException, IOException
    {
        try
        {
            throw new UnsupportedOperationException("todo implement");
        }
        finally
        {
            optimize();
        }
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        String path = fullPath(id);
        // todo we need to acquire a lock here!
        return m_ctx.data().resources.doc(path) != -1;
    }

    @Override
    public boolean has(ObjectVersionID id) throws IOException
    {
        String path = fullPath(id);
        // todo we need to acquire a lock here!
        return m_ctx.data().resources.doc(path) != -1;
    }

    @Override
    public boolean hasAny(ObjectVersionID id) throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.hasAny()");
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.list()");
    }

    @Override
    public Iterable<ObjectVersionID> listVersions() throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.listVersions()");
    }

    @Override
    public void initialize() throws IOException
    {
        if (m_initialized) {
            return;
        }
        m_initialized = true;
        
        if(!dbExists()) {
            createDb();
            optimize();
        }
    }

    @Override
    public void clear() throws IOException
    {
        if (dbExists()) {
            dropDb();
        }
        m_initialized = false;
    }

    @Override
    public boolean supports(Locatable locatable)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped archetyped)
    {
        return true;
    }

    @Override
    public void verifyStatus() throws StatusException
    {
        reportStatus();
    }

    @Override
    public String reportStatus() throws StatusException
    {
        InfoDB cmd = new InfoDB();
        try
        {
            String result = cmd.execute(m_ctx);
            return result;
        }
        catch (BaseXException e)
        {
            throw new StatusException("Cannot get status from BaseX: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        // BaseX only exposes transactions via XQuery Update:
        //   https://mailman.uni-konstanz.de/pipermail/basex-talk/2010-August/000567.html
        return false;
    }

    @Override
    public void begin() throws TransactionException
    {
        // BaseX only exposes transactions via XQuery Update
    }

    @Override
    public void commit() throws TransactionException
    {
        // BaseX only exposes transactions via XQuery Update
    }

    @Override
    public void rollback() throws TransactionException
    {
        // BaseX only exposes transactions via XQuery Update
    }

    private void createDb() throws BaseXException
    {
        new CreateDB(m_name).execute(m_ctx);
        new Add(VERSION_PATH, VERSION_INITIAL);
    }

    private boolean dbExists()
    {
        // todo we need to acquire a lock here!
        return m_ctx.mprop.dbexists(m_name);
    }

    private void dropDb() throws BaseXException
    {
        new DropDB(m_name).execute(m_ctx);
    }
    
    private void optimize() throws BaseXException
    {
        new Optimize().execute(m_ctx);
    }
    
    private String fullPath(String path) {
        return m_path + path;
    }
    
    private String fullPath(UIDBasedID id)
    {
        return fullPath("locatables/"+hPath(id));
    }

    private String fullPath(ObjectVersionID id)
    {
        return fullPath("locatable_versions/"+id.getValue());
    }

    private String fullPath(Locatable locatable)
    {
        return fullPath(hPath(locatable.getUid()));
    }

    private String hPath(UIDBasedID uidBasedID) {
        String v = uidBasedID.getValue();
        if (v.length() < 6) {
            return "00/00/"+v;
        } else {
            return String.format(
                    "%s/%s/%s",
                    v.substring(0, 2),
                    v.substring(2, 4),
                    v.substring(4)
            );
        }
    }
}
