/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.DisposalException;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.InUseException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
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

    public void dispose() throws DisposalException;

    public void clear() throws IOException;
}
