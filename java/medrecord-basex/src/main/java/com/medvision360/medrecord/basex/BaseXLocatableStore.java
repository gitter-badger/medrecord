package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.basex.cmd.Exists;
import com.medvision360.medrecord.basex.cmd.ExistsDB;
import com.medvision360.medrecord.basex.cmd.GetDoc;
import com.medvision360.medrecord.basex.cmd.ListDocs;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Optimize;
import org.basex.core.cmd.Replace;
import org.basex.core.cmd.XQuery;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.VersionTreeID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXLocatableStore extends AbstractLocatableStore implements XQueryStore
{
    // BASIC STRUCTURE
    // ---------------
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
    //   /{m_path}/locatables/hPath({HierObjectID})
    //      <{rmTypeName} archetype_id="...." .../>
    //   /{m_path}/locatable_versions/{ObjectVersionID}
    //      <{rmTypeName} archetype_id="...." .../>
    // faithfully uses Command objects to lock around BaseX, so _should_ be thread-safe
    //
    // XQueries that want to operate on a sub-collection need to specify the dbName in the path, i.e.
    //    collection("{dbName}/{m_path}/locatables/hPath({HierObjectID}")
    //
    // TODO For this reason we should reconsider this setup, to have a distinct database for versions, i.e.
    //
    //   /{dbName}_locatables/{m_path}/hPath({HierObjectID})
    //   /{dbName}_locatable_versions/{m_path}/version
    //   /{dbName}_locatable_versions/{m_path}/{ObjectVersionID}

    // TRANSACTIONS
    // ------------
    // BaseX only exposes transactions via XQuery Update:
    //   https://mailman.uni-konstanz.de/pipermail/basex-talk/2010-August/000567.html
    // so unfortunately we don't support them here. Clients could try to use XQuery Update to get somewhat atomic
    // operations, but that won't help with keeping version info in sync.

    // INDEX OPTIMIZATION
    // ------------------
    // see http://docs.basex.org/wiki/Indexes#Index_Construction
    //   for details about the BaseX index construction/optimization step.
    // Basics:
    //   * we force UPDINDEX to true on the BaseX context so it tries to keep indexes updated
    //   * we run a scheduler that periodically invokes optimize (with intervals of 5 seconds)
    //   * we keep our own 'dirty' flag to check whether to invoke the optimizer, since the BaseX Optimize command will
    //     always do _some_ work even if nothing has changed, which is not what we want
    //   * we register a shutdown hook to get rid of the schedule on shutdown
    //   * we always run optimize immediately when opening a database (i.e. on app startup)

    private final static String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9\\._-]+$";
    private final static String PATH_REGEX = "^[a-zA-Z0-9\\._-]+(?:/[a-zA-Z0-9\\._-]+)*$";
    private final static String ABOUT_INITIAL = "<LocatableStore/>";
    private final static String VERSION_PATH = "/version";
    private final static String VERSION_PRE = "<version>";
    private final static String VERSION_POST = "</version>";
    private final static String VERSION_INITIAL = VERSION_PRE + "1" + VERSION_POST;
    private final static String VERSIONS_Q =
            "for $x in collection(\"%s\")" +
                    "  /*[ //uid/value/text()='%s' ]" +
                    "  return concat(base-uri($x),\"\n\")";
    // with the above query we do N+1 queries to retrieve all locatable versions. We could optimize with a query like
    // the below, but, we would then be pretty much locked into use of XML (rather than also supporting JSON and 
    // whatnot if BaseX is configured that way), and, we would have to figure out how to parse the XML partially and 
    // then feed the remainder into the locatable parser.
//    private final static String VERSIONS_Q =
//            "<locatables>" +
//            "{" +
//            "for $x in collection(\"%s\")" +
//            "/*[ //uid/value/text()='%s' ]" +
//            " return $x" +
//            "}" +
//            "</locatables>";

    private HierObjectID m_systemId;
    private Context m_ctx;
    private LocatableParser m_parser;
    private LocatableSerializer m_serializer;
    private String m_path;
    private boolean m_initialized = false;
    private boolean m_dirty = false;
    ScheduledThreadPoolExecutor m_optimizeExecutor = null;

    public BaseXLocatableStore(Context ctx, LocatableParser parser, LocatableSerializer serializer,
            LocatableSelector locatableSelector, String name, String path)
    {
        super(name, locatableSelector);
        m_systemId = new HierObjectID(name);
        m_ctx = checkNotNull(ctx, "ctx cannot be null");
        // todo results in ArrayIndexOutOfBoundsException m_ctx.prop.set("UPDINDEX", "true");
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
        checkArgument(name.matches(NAME_REGEX), "name has to match regex %s", NAME_REGEX);
        setPath(name, path);

        startConcurrentOptimizer();
    }

    private void setPath(String name, String path)
    {
        m_path = checkNotNull(path, "path cannot be null");
        checkArgument(name.matches(PATH_REGEX), "path has to match regex %s", PATH_REGEX);
        if (!m_path.startsWith("/"))
        {
            m_path = "/" + m_path;
        }
        if (!m_path.endsWith("/"))
        {
            m_path = m_path + "/";
        }
        if ("//".equals(m_path))
        {
            m_path = "/";
        }
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
    public Locatable get(ObjectVersionID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        Locatable result = get(path, id);
        return result;
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");

        String path = fullPath(id);
        if (!has(path))
        {
            throw notFound(id);
        }
        String query = String.format(VERSIONS_Q, dbPath(fullPath("locatable_versions")), id.getValue());
        XQuery cmd = new XQuery(query);
        String queryResult = cmd.execute(m_ctx);
        String[] paths = queryResult.split("\n");
        List<Locatable> result = new ArrayList<>();
        for (int i = 0; i < paths.length; i++)
        {
            path = paths[i].trim();
            String ovidString = path.substring(path.lastIndexOf("/") + 1);
            ObjectVersionID ovid = new ObjectVersionID(ovidString);
            Locatable locatable = get(ovid);
            result.add(locatable);
        }
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
    public void delete(ObjectVersionID id) throws NotFoundException, IOException
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
    public boolean has(ObjectVersionID id) throws IOException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        return has(path);
    }

    @Override
    public boolean hasAny(ObjectVersionID id) throws IOException
    {
        checkNotNull(id, "id cannot be null");
        UID objectID = id.objectID();
        HierObjectID hierObjectID = new HierObjectID(objectID, null);
        return has(hierObjectID);
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        ListDocs cmd = new ListDocs(fullPath("locatables"));
        cmd.execute(m_ctx);
        Iterable<String> resultStrings = cmd.list();
        Iterable<HierObjectID> result = Iterables.transform(resultStrings,
                StringToHierObjectIDFunction.getInstance());
        return result;
    }

    @Override
    public Iterable<ObjectVersionID> listVersions() throws IOException
    {
        ListDocs cmd = new ListDocs(fullPath("locatable_versions"));
        cmd.execute(m_ctx);
        Iterable<String> resultStrings = cmd.list();
        Iterable<ObjectVersionID> result = Iterables.transform(resultStrings,
                StringToObjectVersionIDFunction.getInstance());
        return result;
    }

    @Override
    public void initialize() throws IOException
    {
        if (m_initialized)
        {
            return;
        }
        m_initialized = true;

        if (!dbExists())
        {
            createDb();
            optimizeNow();
        }
    }

    @Override
    public void clear() throws IOException
    {
        if (dbExists())
        {
            dropDb();
        }
        m_initialized = false;
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
    public String toString()
    {
        return String.format("[%s:%s]", this.getClass().getSimpleName(), getName());
    }

    @Override
    public Iterable<Locatable> list(String XQuery) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        throw new UnsupportedOperationException("todo implement BaseXLocatableStore.list()");
    }

    @Override
    public void query(String XQuery, OutputStream os) throws NotSupportedException, IOException
    {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        XQuery cmd = new XQuery(XQuery);
        cmd.execute(m_ctx, os);
    }

    ///
    /// Helpers
    ///

    private <T extends Throwable> T wrap(T e, Object argument) throws NotFoundException
    {
        if (e instanceof NotFoundException)
        {
            throw (NotFoundException) e;
        }
        String message = e.getMessage();
        if (
                message != null && (
                        message.contains("not found") ||
                                message.contains("yields no documents")
                ))
        {
            throw notFound(argument, e);
        }
        return e;
    }

    private void createDb() throws BaseXException
    {
        new CreateDB(m_name).execute(m_ctx);
        String root = m_path.substring(0, m_path.length() - 1);
        new Add(root, ABOUT_INITIAL).execute(m_ctx);
        new Add(fullPath(VERSION_PATH), VERSION_INITIAL).execute(m_ctx);
    }

    private boolean dbExists() throws BaseXException
    {
        ExistsDB cmd = new ExistsDB(m_name);
        cmd.execute(m_ctx);
        return cmd.exists();
    }

    private void dropDb() throws BaseXException
    {
        new DropDB(m_name).execute(m_ctx);
    }

    private void optimize()
    {
        m_dirty = true;
    }

    private void optimizeNow() throws BaseXException
    {
        new Optimize().execute(m_ctx);
    }

    private void startConcurrentOptimizer()
    {
        m_optimizeExecutor = new ScheduledThreadPoolExecutor(1);
        m_optimizeExecutor.scheduleWithFixedDelay(new Runnable()
        {
            @Override
            public void run()
            {
                if (!m_initialized)
                {
                    return;
                }

                if (m_dirty)
                {
                    try
                    {
                        optimizeNow();
                    }
                    catch (BaseXException e)
                    {
                        // too bad
                    }
                    m_dirty = false;
                }
            }
        }, 10, 5, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                if (m_optimizeExecutor != null)
                {
                    m_optimizeExecutor.shutdown();
                    try
                    {
                        boolean terminated = m_optimizeExecutor.awaitTermination(5, TimeUnit.SECONDS);
                        if (!terminated)
                        {
                            m_optimizeExecutor.shutdownNow();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        // yessir
                    }
                }
            }
        });
    }

    private String path(UIDBasedID id)
    {
        return "locatables/" + hPath(id);
    }

    private String path(ObjectVersionID id)
    {
        return "locatable_versions/" + id.getValue();
    }

    private String path(Locatable locatable)
    {
        return "locatables/" + hPath(locatable.getUid());
    }

    private String fullPath(String path)
    {
        return m_path + path;
    }

    private String fullPath(UIDBasedID id)
    {
        return fullPath(path(id));
    }

    private String fullPath(ObjectVersionID id)
    {
        return fullPath(path(id));
    }

    private String fullPath(Locatable locatable)
    {
        return fullPath(path(locatable));
    }

    private String dbPath(String path)
    {
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }
        return m_name + "/" + path;
    }

    private String hPath(UIDBasedID uidBasedID)
    {
        String v = uidBasedID.getValue();
        if (v.length() < 6)
        {
            return "00/00/" + v;
        }
        else
        {
            return String.format(
                    "%s/%s/%s",
                    v.substring(0, 2),
                    v.substring(2, 4),
                    v.substring(4)
            );
        }
    }

    protected VersionTreeID nextVersion() throws BaseXException, UnsupportedEncodingException
    {
        // todo this needs a lock around the whole thing, so add a kind of AtomicIncrement command
        // inspired by the Execute command
        XQuery query = new XQuery(VERSION_PATH);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        query.execute(m_ctx, os);
        String xml = os.toString("UTF-8");
        xml = xml.replace(VERSION_PRE, "");
        xml = xml.replace(VERSION_POST, "");
        long version = Long.parseLong(xml);
        VersionTreeID result = new VersionTreeID("" + version);

        version++;

        Replace replace = new Replace(fullPath(VERSION_PATH), VERSION_PRE + version + VERSION_POST);
        replace.execute(m_ctx);

        return result;
    }

    protected ObjectVersionID newObjectVersionID(UID uid) throws BaseXException, UnsupportedEncodingException
    {
        return new ObjectVersionID(uid, m_systemId, nextVersion());
    }

    private boolean has(String path) throws BaseXException
    {
        // note this does not invoke ensureNotDirty(), so with concurrent optimize,
        // insert()/update()/delete() may be a little, err, dirty
        Exists cmd = new Exists(path);
        cmd.execute(m_ctx);
        return cmd.exists();
    }

    private Locatable get(String path, Object argument) throws IOException, NotFoundException, ParseException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // if (path.startsWith("/"))
        // {
        //     path = path.substring(1);
        // }
        // XQuery cmd = new XQuery("doc(\""+m_name+"/"+path+"\")");
        GetDoc cmd = new GetDoc(path);

        try
        {
            cmd.execute(m_ctx, os);
        }
        catch (BaseXException e)
        {
            throw wrap(e, argument);
        }

        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        return m_parser.parse(is);
    }

    private Locatable replace(Locatable locatable, String path) throws IOException, SerializeException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(locatable, os);
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is;
        Replace cmd;

        // store the locatable as a new version
        ObjectVersionID ovid = newObjectVersionID(locatable.getUid().root());
        String ovidPath = fullPath(ovid);
        is = new ByteArrayInputStream(buffer);
        cmd = new Replace(ovidPath);
        cmd.setInput(is);

        os = new ByteArrayOutputStream();
        try
        {
            cmd.execute(m_ctx, os);
        }
        finally
        {
            optimize();
        }

        // store the locatable
        is = new ByteArrayInputStream(buffer);
        cmd = new Replace(path);
        cmd.setInput(is);

        os = new ByteArrayOutputStream();
        try
        {
            cmd.execute(m_ctx, os);
        }
        finally
        {
            optimize();
        }

        return locatable;
    }

    private void delete(String path) throws IOException
    {
        Delete cmd = new Delete(path);

        try
        {
            cmd.execute(m_ctx);
        }
        finally
        {
            optimize();
        }
    }

}
