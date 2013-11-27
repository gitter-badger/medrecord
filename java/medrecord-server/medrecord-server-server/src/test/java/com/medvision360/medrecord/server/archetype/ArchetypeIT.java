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
package com.medvision360.medrecord.server.archetype;

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.archetype.ArchetypeRequest;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.client.archetype.ArchetypeListResourceListArchetypesParams;
import com.medvision360.medrecord.client.archetype.ArchetypeResource;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.server.AbstractServerTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ArchetypeIT extends AbstractServerTest
{
    private ArchetypeResource resource(String id) throws Exception
    {
        return new ArchetypeResource(m_resourceConfig, id);
    }

    @Test
    public void getArchetypeWithInvalidNameThrowsInvalidArchetypeIDException()
            throws Exception
    {
        String archetypeName = "invalid_archetype_id";
        try
        {
            resource(archetypeName).getArchetype();
            fail("Exception expected...");
        }
        catch(InvalidArchetypeIDException e)
        {
            assertTrue(e.getMessage().contains(archetypeName));
        }
    }

    @Test
    public void getArchetypeWithUnknownArchetypeIDThrowsNotFoundException()
            throws Exception
    {
        String archetypeName = "openEHR-EHR-COMPOSITION.non_existent_archetype.v1";
        try
        {
            resource(archetypeName).getArchetype();
            fail("Exception expected...");
        }
        catch(NotFoundException e)
        {
            assertTrue(e.getMessage().contains(archetypeName));
        }
    }

    @Test
    public void deleteArchetypeWithInvalidNameThrowsInvalidArchetypeIDException()
            throws Exception
    {
        String archetypeName = "invalid_archetype_id";
        try
        {
            resource(archetypeName).deleteArchetype();
            fail("Exception expected...");
        }
        catch(InvalidArchetypeIDException e)
        {
            assertTrue(e.getMessage().contains(archetypeName));
        }
    }
    
    @Test
    public void crudOperationsOnArchetype() throws Exception
    {
        String archetypeName = "openEHR-EHR-OBSERVATION.blood_pressure.v1";
        ArchetypeRequest request = loadArchetype(archetypeName);

        ArchetypeResource resource = resource(archetypeName);
        ArchetypeResult result;
        IDList list;

        // empty list
        clear(); // call clear again since another test may have added data
        list = m_archetypeListResource.listArchetypes();
        assertEquals(0, list.getIds().size());

        // POST
        m_archetypeListResource.postArchetype(request);
        
        // GET
        result = resource.getArchetype();
        assertNotNull(result);
        assertNotNull(result.getArchetypeId());
        assertEquals(archetypeName, result.getArchetypeId());
        assertNotNull(result.getAdl());
        assertTrue(result.getAdl().contains("blood_pressure"));
        assertTrue(result.getAdl().trim().startsWith("archetype"));
        
        // LIST
        list = m_archetypeListResource.listArchetypes();
        assertNotNull(list);
        assertNotNull(list.getIds());
        assertEquals(1, list.getIds().size());
        assertTrue(list.getIds().contains(archetypeName));
        
        // DELETE
        resource.deleteArchetype();
        try
        {
            resource.getArchetype();
            fail("Exception expected");
        }
        catch (NotFoundException e)
        {
            assertTrue(e.getMessage().contains(archetypeName));
        }
        list = m_archetypeListResource.listArchetypes();
        assertEquals(0, list.getIds().size());
        
        // re-POST
        m_archetypeListResource.postArchetype(request);
        resource.getArchetype();
    }

    @Test
    public void listArchetypesWithRegexMatch() throws Exception
    {
        String bloodGlucoseArchetype = "openEHR-EHR-OBSERVATION.lab_test-blood_glucose.v1";
        ensureArchetype(bloodGlucoseArchetype);
        
        String bloodMatchArchetype = "openEHR-EHR-OBSERVATION.lab_test-blood_match.v1";
        ensureArchetype(bloodMatchArchetype);
        
        String histopathologyArchetype = "openEHR-EHR-OBSERVATION.lab_test-histopathology.v1";
        ensureArchetype(histopathologyArchetype);

        ArchetypeListResourceListArchetypesParams params = new ArchetypeListResourceListArchetypesParams();
        params.setQ("lab_test-blood.*?v[12]");
        IDList list = m_archetypeListResource.listArchetypes(params);
        assertNotNull(list.getIds());
        assertTrue(list.getIds().contains(bloodGlucoseArchetype));
        assertTrue(list.getIds().contains(bloodMatchArchetype));
        assertFalse(list.getIds().contains(histopathologyArchetype));
        
        // try full match with ^ $
        params = new ArchetypeListResourceListArchetypesParams();
        params.setQ("^openEHR-EHR-OBSERVATION\\.lab_test-blood.*\\.v1$");
        list = m_archetypeListResource.listArchetypes(params);
        assertNotNull(list.getIds());
        assertTrue(list.getIds().contains(bloodGlucoseArchetype));
    }
    
    @Test
    public void listArchetypesWithInvalidRegexThrowsPatternException() throws Exception
    {
        ArchetypeListResourceListArchetypesParams params = new ArchetypeListResourceListArchetypesParams();
        params.setQ("openEHR-invalid-regex.[.v1");

        try
        {
            m_archetypeListResource.listArchetypes(params);
            fail("Exception expected");
        }
        catch (PatternException e)
        {
            assertTrue(e.getMessage().contains("character class"));
        }
    }
}
