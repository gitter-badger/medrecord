package com.medvision360.medrecord.basex;

import java.io.IOException;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.basex.core.Context;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

public class BaseXArchetypeStore extends AbstractBaseXStore implements ArchetypeStore
{
    public BaseXArchetypeStore(Context ctx, String name, String path)
    {
        super(ctx, name, path);
    }

    @Override
    public Archetype get(Archetyped archetypeDetails) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.get()");
    }

    @Override
    public Archetype get(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.get()");
    }

    @Override
    public boolean has(Archetyped archetypeDetails) throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.has()");
    }

    @Override
    public boolean has(ArchetypeID archetypeID) throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.has()");
    }

    @Override
    public void insert(Archetype archetype) throws DuplicateException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.insert()");
    }

    @Override
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.delete()");
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.lock()");
    }

    @Override
    public Iterable<ArchetypeID> list() throws IOException
    {
        throw new UnsupportedOperationException("todo implement BaseXArchetypeStore.list()");
    }
}
