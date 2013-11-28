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
package com.medvision360.medrecord.server.ehr;

import java.io.ByteArrayOutputStream;

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.InvalidEHRIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.client.ehr.EHRListResourceListEHRsParams;
import com.medvision360.medrecord.client.ehr.EHRResource;
import com.medvision360.medrecord.client.ehr.EHRUndeleteResource;
import com.medvision360.medrecord.pv.PVSerializer;
import com.medvision360.medrecord.server.AbstractServerTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openehr.rm.ehr.EHRStatus;
import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;

@RunWith(JUnit4.class)
public class EhrIT extends AbstractServerTest
{
    public final static String COLLECTION = "unittest";
    public final static String ARCHETYPE = "unittest-EHR-EHRSTATUS.ehrstatus.v1";

    private EHRResource resource(String id) throws Exception
    {
        return new EHRResource(m_resourceConfig, id);
    }
    
    private EHRUndeleteResource undeleteResource(String id) throws Exception
    {
        return new EHRUndeleteResource(m_resourceConfig, id);
    }
    
    private void ensureEHRStatusArchetype() throws Exception
    {
        ensureArchetype(COLLECTION, ARCHETYPE);
    }
    
    @Test
    public void getEHRWithInvalidNameThrowsInvalidEHRIDException()
            throws Exception
    {
        String id = "invalid ehr id";
        try
        {
            resource(id).getEHR();
            fail("Exception expected...");
        }
        catch(InvalidEHRIDException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void getEHRWithUnknownEHRIDThrowsNotFoundException()
            throws Exception
    {
        String id = "D2138B66-D181-465E-B744-BB3B33CBD181";
        try
        {
            resource(id).getEHR();
            fail("Exception expected...");
        }
        catch(NotFoundException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void deleteEHRWithInvalidNameThrowsInvalidEHRIDException()
            throws Exception
    {
        String id = "invalid ehr id";
        try
        {
            resource(id).deleteEHR();
            fail("Exception expected...");
        }
        catch(InvalidEHRIDException e)
        {
            assertTrue(e.getMessage().contains(id));
        }
    }

    @Test
    public void createEHRWithEmptyStatusThrowsClientParseException()
            throws Exception
    {
        try
        {
            Representation request = toRequest(null);
            m_ehrListResource.postEHR(request);
            fail("Exception expected...");
        }
        catch(ClientParseException e)
        {
        }
    }
    
    @Test
    public void crudOperationsOnEHR() throws Exception
    {
        EHR result;
        IDList list;
        Representation request;
        EHRListResourceListEHRsParams params;

        // empty list
        clear(); // call clear again since another test may have added data
        
        // clear() means no archetypes, but we need this...
        ensureEHRStatusArchetype();
        
        list = m_ehrListResource.listEHRs();
        assertEquals(0, list.getIds().size());

        // POST
        request = toRequest(m_parent);
        ID id = m_ehrListResource.postEHR(request);
        String idString = id.getId();
        
        // GET
        EHRResource resource = resource(idString);
        result = resource.getEHR();
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(idString, result.getId());
        assertNotNull(result.getStatusId());
        
        // LIST
        list = m_ehrListResource.listEHRs();
        assertNotNull(list);
        assertNotNull(list.getIds());
        assertEquals(1, list.getIds().size());
        assertTrue(list.getIds().contains(idString));
        
        // DELETE
        resource.deleteEHR();
        try
        {
            resource.getEHR();
            fail("Exception expected");
        }
        catch (NotFoundException e)
        {
            assertTrue(e.getMessage().contains(idString));
        }
        params = new EHRListResourceListEHRsParams();
        params.setExcludeDeleted("true");
        list = m_ehrListResource.listEHRs(params);
        assertEquals(0, list.getIds().size());
        
        params = new EHRListResourceListEHRsParams();
        params.setExcludeDeleted("false");
        list = m_ehrListResource.listEHRs(params);
        assertEquals(1, list.getIds().size());
        
        // UNDELETE
        undeleteResource(idString).undeleteEHR();
        resource.getEHR();
    }

    protected Representation toRequest(EHRStatus ehrStatus) throws Exception
    {
        PVSerializer pvSerializer = new PVSerializer();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        pvSerializer.serialize(ehrStatus, os);
        byte[] serialized = os.toByteArray();
        
        Representation representation = new ByteArrayRepresentation(serialized, MediaType.APPLICATION_JSON, 
                serialized.length);
        return representation;
    }

    // todo test list locatables
}
