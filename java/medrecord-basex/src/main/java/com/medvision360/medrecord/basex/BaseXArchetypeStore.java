package com.medvision360.medrecord.basex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.basex.cmd.ListDocs;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.basex.core.Context;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXArchetypeStore extends AbstractBaseXStore implements ArchetypeStore
{
    private final Cache<ArchetypeID, WrappedArchetype> m_cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    private final ArchetypeParser m_parser;
    private final ArchetypeSerializer m_serializer;

    public BaseXArchetypeStore(Context ctx, ArchetypeParser parser, ArchetypeSerializer serializer,
            String name, String path)
    {
        super(ctx, name, path);
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
    }

    @Override
    public void clear() throws IOException
    {
        super.clear();
        m_cache.invalidateAll();
    }

    @Override
    public WrappedArchetype get(Archetyped archetypeDetails) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(archetypeDetails, "archetypeDetails cannot be null");
        return get(archetypeDetails.getArchetypeId());
    }

    @Override
    public WrappedArchetype get(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        WrappedArchetype cache = m_cache.getIfPresent(archetypeID);
        if (cache != null)
        {
            return cache;
        }
        
        String path = fullPath(archetypeID);
        WrappedArchetype result = get(path, archetypeID);
        return result;
    }

    @Override
    public boolean has(Archetyped archetypeDetails) throws IOException
    {
        checkNotNull(archetypeDetails, "archetypeDetails cannot be null");
        return has(archetypeDetails.getArchetypeId());
    }

    @Override
    public boolean has(ArchetypeID archetypeID) throws IOException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        WrappedArchetype cache = m_cache.getIfPresent(archetypeID);
        if (cache != null)
        {
            return true;
        }

        String path = fullPath(archetypeID);
        return has(path);
    }

    @Override
    public WrappedArchetype insert(WrappedArchetype archetype) throws DuplicateException, IOException, SerializeException
    {
        checkNotNull(archetype, "archetype cannot be null");
        String path = fullPath(archetype.getArchetype().getArchetypeId());
        if (has(path))
        {
            throw duplicate(archetype);
        }
        return replace(archetype, path);
    }

    @Override
    public WrappedArchetype insert(Archetype archetype) throws DuplicateException, IOException, SerializeException
    {
        checkNotNull(archetype, "archetype cannot be null");
        return insert(new WrappedArchetype(null, archetype));
    }

    @Override
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException, ParseException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        WrappedArchetype wrappedArchetype = get(archetypeID);
        
        if (wrappedArchetype.isLocked())
        {
            throw new InUseException(String.format("Archetype %s is in use",
                    wrappedArchetype.getArchetype().getArchetypeId().getValue()));
        }
        String path = fullPath(archetypeID);
        delete(path);
        m_cache.invalidate(archetypeID);
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(archetypeID, "archetypeID cannot be null");
        WrappedArchetype archetype = get(archetypeID);
        
        archetype = new WrappedArchetype(archetype.getAsString(), archetype.getArchetype(), true);
        String path = fullPath(archetype.getArchetype().getArchetypeId());
        try
        {
            replace(archetype, path);
        }
        catch (SerializeException e)
        {
            throw new IOException(e);
        }
    }

    @Override
    public Iterable<ArchetypeID> list() throws IOException
    {
        String path = fullPath("archetype");
        return listArchetypes(path);
    }

    ///
    /// Helpers
    ///

    protected WrappedArchetype get(String path, ArchetypeID argument)
            throws IOException, NotFoundException, ParseException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        get(path, argument, os); // calls initialize()
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        WrappedArchetype result = m_parser.parse(is);
        m_cache.put(result.getArchetype().getArchetypeId(), result);
        return result;
    }

    protected WrappedArchetype replace(WrappedArchetype wrappedArchetype, String path)
            throws IOException, SerializeException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(wrappedArchetype, os);
        byte[] buffer = os.toByteArray();
        String asString = new String(buffer);
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        replace(path, is); // calls initialize()
        WrappedArchetype result = new WrappedArchetype(asString, wrappedArchetype.getArchetype(),
                wrappedArchetype.isLocked());
        m_cache.put(wrappedArchetype.getArchetype().getArchetypeId(), result);
        return result;
    }

    protected Iterable<ArchetypeID> listArchetypes(String path) throws IOException
    {
        initialize();
        ListDocs cmd = new ListDocs(path);
        cmd.execute(m_ctx);
        Iterable<String> resultStrings = cmd.list();
        Iterable<ArchetypeID> result = Iterables.transform(resultStrings,
                StringToArchetypeIDFunction.getInstance());
        return result;
    }
}
