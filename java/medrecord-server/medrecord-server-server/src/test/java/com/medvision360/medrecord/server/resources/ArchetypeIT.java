package com.medvision360.medrecord.server.resources;

import com.medvision360.medrecord.api.archetype.ArchetypeList;
import com.medvision360.medrecord.api.archetype.ArchetypeRequest;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.client.archetype.ArchetypeListResourceListArchetypesParams;
import com.medvision360.medrecord.client.archetype.ArchetypeResource;
import com.medvision360.medrecord.spi.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ArchetypeIT extends AbstractIntegrationTest
{
    private ArchetypeResource resource(String id) throws Exception
    {
        return  new ArchetypeResource(m_resourceConfig, id);
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
        ArchetypeList list;

        // empty list
        clear(); // call clear again since another test may have added data
        list = m_archetypeListResource.listArchetypes();
        assertEquals(0, list.getArchetypes().size());

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
        assertNotNull(list.getArchetypes());
        assertEquals(1, list.getArchetypes().size());
        assertTrue(list.getArchetypes().contains(archetypeName));
        
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
        assertEquals(0, list.getArchetypes().size());
        
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
        params.setQueryArgument("q", "lab_test-blood.*?v[12]");
        ArchetypeList list = m_archetypeListResource.listArchetypes(params);
        assertNotNull(list.getArchetypes());
        assertTrue(list.getArchetypes().contains(bloodGlucoseArchetype));
        assertTrue(list.getArchetypes().contains(bloodMatchArchetype));
        assertFalse(list.getArchetypes().contains(histopathologyArchetype));
        
        // try full match with ^ $
        params = new ArchetypeListResourceListArchetypesParams();
        params.setQueryArgument("q", "^openEHR-EHR-OBSERVATION\\.lab_test-blood.*\\.v1$");
        list = m_archetypeListResource.listArchetypes(params);
        assertNotNull(list.getArchetypes());
        assertTrue(list.getArchetypes().contains(bloodGlucoseArchetype));
    }
    
    @Test
    public void listArchetypesWithInvalidRegexThrowsPatternException() throws Exception
    {
        ArchetypeListResourceListArchetypesParams params = new ArchetypeListResourceListArchetypesParams();
        params.setQueryArgument("q", "openEHR-invalid-regex.[.v1");

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
