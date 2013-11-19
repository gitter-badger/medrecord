package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.basex.cmd.Exists;
import com.medvision360.medrecord.basex.cmd.ExistsDB;
import com.medvision360.medrecord.basex.cmd.GetDoc;
import com.medvision360.medrecord.basex.cmd.ListDocs;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Delete;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.Optimize;
import org.basex.core.cmd.Replace;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBaseXStore extends AbstractLocatableStore
{
    // BASIC STRUCTURE
    // ---------------
    // one store = one database
    // name is used as database name
    // note basex does not have hierarchical collections
    // faithfully uses Command objects to lock around BaseX, so _should_ be thread-safe
    //
    // XQueries that want to operate on a sub-collection need to specify the dbName in the path, i.e.
    //    collection("{dbName}/{m_path}/locatables/hPath({HierObjectID}")

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

    public static final int OPTIMIZER_INITIAL_DELAY = 10;
    public static final int OPTIMIZER_DELAY = 5;

    protected final static String NAME_REGEX = "^[a-zA-Z][a-zA-Z0-9\\._-]+$";
    protected final static String PATH_REGEX = "^[a-zA-Z0-9\\._-]+(?:/[a-zA-Z0-9\\._-]+)*$";
    protected final static String ABOUT_INITIAL = "<RecordStore/>";

    protected Context m_ctx;
    protected String m_path;
    protected boolean m_initialized = false;
    protected boolean m_dirty = false;
    protected ScheduledThreadPoolExecutor m_optimizeExecutor = null;

    public AbstractBaseXStore(Context ctx, LocatableSelector locatableSelector, String name, String path)
    {
        super(name, locatableSelector);
        m_ctx = checkNotNull(ctx, "ctx cannot be null");
        // todo results in ArrayIndexOutOfBoundsException m_ctx.prop.set("UPDINDEX", "true");
        checkArgument(name.matches(NAME_REGEX), "name has to match regex %s", NAME_REGEX);
        setPath(name, path);

        //startConcurrentOptimizer();
    }

    public AbstractBaseXStore(Context ctx, String name, String path)
    {
        this(ctx, LocatableSelectorBuilder.any(), name, path);
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
        dbOpen();
    }

    public void clear() throws IOException
    {
        if (dbExists())
        {
            dropDb();
        }
        m_initialized = false;
    }

    public void verifyStatus() throws StatusException
    {
        reportStatus();
    }

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

    ///
    /// Helpers
    ///

    protected void setPath(String name, String path)
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

    protected <T extends Throwable> T wrap(T e, Object argument) throws NotFoundException
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

    protected void createDb() throws BaseXException
    {
        new CreateDB(m_name).execute(m_ctx);
        String root = m_path.substring(0, m_path.length() - 1);
        new Add(root, ABOUT_INITIAL).execute(m_ctx);
    }

    protected boolean dbExists() throws BaseXException
    {
        ExistsDB cmd = new ExistsDB(m_name);
        cmd.execute(m_ctx);
        return cmd.exists();
    }

    protected void dbOpen() throws BaseXException
    {
        new Open(m_name).execute(m_ctx);
    }

    protected void dropDb() throws BaseXException
    {
        new DropDB(m_name).execute(m_ctx);
    }

    protected void optimize()
    {
        m_dirty = true;
    }

    protected void optimizeNow() throws BaseXException
    {
        // m_initialized should be true
        new Optimize().execute(m_ctx);
    }

    protected void startConcurrentOptimizer()
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
        }, OPTIMIZER_INITIAL_DELAY, OPTIMIZER_DELAY, TimeUnit.SECONDS);

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

    protected String path(UIDBasedID id)
    {
        return "locatables/" + hPath(id);
    }

    protected String path(Locatable locatable)
    {
        return "locatables/" + hPath(locatable.getUid());
    }

    protected String path(EHR EHR)
    {
        return "ehr/" + hPath(EHR.getEhrID());
    }

    protected String path(EHR EHR, Locatable locatable)
    {
        return path(EHR) + "/" + locatable.getArchetypeDetails().getArchetypeId().rmEntity() + "/" +
                locatable.getUid().getValue();
    }

    protected String path(ArchetypeID archetypeID)
    {
        return "archetype/" + archetypeID.getValue();
    }

    protected String fullPath(String path)
    {
        return m_path + path;
    }

    protected String fullPath(UIDBasedID id)
    {
        return fullPath(path(id));
    }

    protected String fullPath(Locatable locatable)
    {
        return fullPath(path(locatable));
    }

    protected String fullPath(EHR EHR)
    {
        return fullPath(path(EHR));
    }

    protected String fullPath(EHR EHR, Locatable locatable)
    {
        return fullPath(path(EHR, locatable));
    }

    protected String fullPath(ArchetypeID archetypeID)
    {
        return fullPath(path(archetypeID));
    }

    protected String hPath(UIDBasedID uidBasedID)
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
                    v
            );
        }
    }

    protected boolean has(String path) throws IOException
    {
        initialize();
        // note this does not invoke ensureNotDirty(), so with concurrent optimize,
        // insert()/update()/delete() may be a little, err, dirty
        Exists cmd = new Exists(path);
        cmd.execute(m_ctx);
        return cmd.exists();
    }

    protected void get(String path, Object argument, ByteArrayOutputStream os) throws IOException, NotFoundException
    {
        initialize();
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
    }

    protected void replace(String path, ByteArrayInputStream is) throws IOException
    {
        initialize();
        ByteArrayOutputStream os;
        Replace cmd = new Replace(path);
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
    }

    protected void replace(String path) throws IOException
    {
        initialize();
        Replace cmd = new Replace(path, "<empty/>");
        try
        {
            cmd.execute(m_ctx);
        }
        finally
        {
            optimize();
        }
    }

    protected void delete(String path) throws IOException
    {
        initialize();
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

    protected Iterable<HierObjectID> list(String path) throws IOException
    {
        initialize();
        ListDocs cmd = new ListDocs(path);
        cmd.execute(m_ctx);
        Iterable<String> resultStrings = cmd.list();
        Iterable<HierObjectID> result = Iterables.transform(resultStrings,
                StringToHierObjectIDFunction.getInstance());
        return result;
    }
}
