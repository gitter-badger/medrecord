package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

public interface ArchetypeStore extends TransactionalService, StatusService // todo javadoc / api spec / exceptions
{
    public WrappedArchetype get(Archetyped archetypeDetails) throws NotFoundException, IOException, ParseException;
    
    public WrappedArchetype get(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException;
    
    public boolean has(Archetyped archetypeDetails) throws IOException;
    
    public boolean has(ArchetypeID archetypeID) throws IOException;
    
    public WrappedArchetype insert(WrappedArchetype archetype) throws DuplicateException, IOException, SerializeException;
    
    public WrappedArchetype insert(Archetype archetype) throws DuplicateException, IOException, SerializeException;
    
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException, ParseException;
    
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException;
    
    public Iterable<ArchetypeID> list() throws IOException;

    public void initialize() throws IOException;

    public void clear() throws IOException;
}
