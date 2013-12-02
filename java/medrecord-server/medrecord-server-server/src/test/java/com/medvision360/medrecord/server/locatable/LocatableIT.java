package com.medvision360.medrecord.server.locatable;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.InvalidLocatableIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.client.locatable.LocatableResource;
import com.medvision360.medrecord.server.AbstractServerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openehr.rm.common.archetyped.Locatable;
import org.restlet.representation.Representation;

@RunWith(JUnit4.class)
public class LocatableIT extends AbstractServerTest
{
    @Test
    public void getLocatableWithInvalidNameThrowsInvalidLocatableIDException()
            throws Exception
    {
        String id = "invalid locatable id";
        try
        {
            locatableResource(id).getLocatable();
            fail("Exception expected...");
        }
        catch(InvalidLocatableIDException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void getLocatableWithUnknownLocatableIDThrowsNotFoundException()
            throws Exception
    {
        String id = "D2138B66-D181-465E-B744-BB3B33CBD181";
        try
        {
            locatableResource(id).getLocatable();
            fail("Exception expected...");
        }
        catch(NotFoundException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void deleteLocatableWithInvalidNameThrowsInvalidLocatableIDException()
            throws Exception
    {
        String id = "invalid locatable id";
        try
        {
            locatableResource(id).deleteLocatable();
            fail("Exception expected...");
        }
        catch(InvalidLocatableIDException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void createLocatableWithEmptyStatusThrowsClientParseException()
            throws Exception
    {
        try
        {
            Representation request = toJsonRequest(null);
            m_locatableListResource.postLocatable(request);
            fail("Exception expected...");
        }
        catch(ClientParseException e)
        {
        }
    }
    
    @Test
    public void crudOperationsOnLocatable() throws Exception
    {
        Locatable locatable;
        Representation result;
        IDList list;
        Representation request;

        // empty list
        clear(); // call clear again since another test may have added data
        
        // clear() means no archetypes, but we need this...
        ensureEHRStatusArchetype();
        ensureCompositionArchetype();
        ensureAdminEntryArchetype();
        
        list = m_locatableListResource.listLocatables();
        assertEquals(0, list.getIds().size());

        // POST
        locatable = makeLocatable();
        request = toJsonRequest(locatable);
        ID id = m_locatableListResource.postLocatable(request);
        String idString = id.getId();
        
        // GET
        LocatableResource resource = locatableResource(idString);
        result = resource.getLocatable();
        assertNotNull(result);
        Locatable retrieved = fromJsonRequest(result);
        assertEqualish(locatable, retrieved);
        
        // LIST
        list = m_locatableListResource.listLocatables();
        assertNotNull(list);
        assertNotNull(list.getIds());
        assertEquals(1, list.getIds().size());
        assertTrue(list.getIds().contains(idString));
        
        // DELETE
        resource.deleteLocatable();
        try
        {
            resource.getLocatable();
            fail("Exception expected");
        }
        catch (NotFoundException e)
        {
            assertTrue(e.getMessage().contains(idString));
        }
        list = m_locatableListResource.listLocatables();
        assertEquals(0, list.getIds().size());
    }
    
    // todo test link to EHR
    
    // todo test patch

}
