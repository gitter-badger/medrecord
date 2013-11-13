/** @file
 * This file belongs to the MEDvision360 Identity Server Integration
 * Tests and contains the OrganizationIT class.
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.test.demo;

import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.lib.api.ServiceUnavailableException;
import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.lib.common.converter.ExtendedJacksonConverter;
import com.medvision360.medrecord.api.archetype.ArchetypeNotFoundException;
import com.medvision360.medrecord.api.demo.MrDemoResult;
import com.medvision360.medrecord.client.demo.MrDemoResource;
import com.medvision360.medrecord.client.archetype.ArchetypeResource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.resource.ResourceException;

import static org.junit.Assert.*;

public class DemoIT
{
    final ClientResourceConfig m_resourceConfig = new ClientResourceConfig(
        System.getProperty("integrationtest.service.url") + "/v2"
    );

    @BeforeClass
    public static void setup()
    {
        ExtendedJacksonConverter.register();
    }

    @Test
    public void testHello() throws MissingParameterException
    {
        final String test = "Hello There!";
        final MrDemoResource resource = new MrDemoResource(m_resourceConfig);
        final MrDemoResult hello = resource.getResult(test);
        assertEquals(test, hello.getStuff());
    }

    @Test
    public void testArchetype()
            throws MissingParameterException, ServiceUnavailableException, ArchetypeNotFoundException
    {
        try
        {
            final ArchetypeResource resource = new ArchetypeResource(m_resourceConfig);
            final String archetype = resource.getArchetype("123");
            fail("Exception expected...");
        }
        catch(ResourceException e)
        {
        }
    }

    @Test
    public void testArchetype2()
            throws MissingParameterException, ServiceUnavailableException, ArchetypeNotFoundException
    {
        final ArchetypeResource resource = new ArchetypeResource(m_resourceConfig);
        final String archetype = resource.getArchetype("test");
        assertEquals("lots of text", archetype);
    }

    @Test
    public void testArchetype3()
            throws MissingParameterException, ServiceUnavailableException, ArchetypeNotFoundException
    {
        try
        {
            final ArchetypeResource resource = new ArchetypeResource(m_resourceConfig);
            final String archetype = resource.getArchetype("iae");
            fail("Exception expected...");
        }
        catch(ResourceException e)
        {
        }
    }

}

