package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

public interface ArchetypeStore extends TransactionalService, StatusService // todo javadoc / api spec / exceptions
{
    public Archetype get(Archetyped archetypeDetails) throws NotFoundException, IOException;
    
    public Archetype get(ArchetypeID archetypeID) throws NotFoundException, IOException;
    
    public boolean has(Archetyped archetypeDetails) throws IOException;
    
    public boolean has(ArchetypeID archetypeID) throws IOException;
    
    public void insert(Archetype archetype) throws DuplicateException, IOException;
    
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException;
    
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException;
    
    public Iterable<ArchetypeID> list() throws IOException;

    public void initialize() throws IOException;

    public void clear() throws IOException;
}
