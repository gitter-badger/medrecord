/** @file
 * This file belongs to the MEDvision360 Identity Server Integration
 * Tests and contains the OrganizationIT class.
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.server.resources;

import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.lib.common.converter.ExtendedJacksonConverter;
import com.medvision360.medrecord.client.archetype.ArchetypeResource;
import com.medvision360.medrecord.server.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.spi.exceptions.MissingParameterException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ArchetypeIT
{
    private static ClientResourceConfig m_resourceConfig;
    private ArchetypeResource m_resource;
    
    @BeforeClass
    public static void classSetUp()
    {
        ExtendedJacksonConverter.register();
        //noinspection SpellCheckingInspection
        m_resourceConfig = new ClientResourceConfig(
               System.getProperty("integrationtest.service.url") + "/v2"
           );
    }

    @Before
    public void setUp()
    {
        m_resource = new ArchetypeResource(m_resourceConfig);
    }
    
    @Test
    public void getArchetypeWithNullThrowsMissingParameterException()
            throws Exception
    {
        try
        {
            m_resource.getArchetype(null);
            fail("Exception expected...");
        }
        catch(MissingParameterException e)
        {
        }
    }

    @Test
    public void getArchetypeWithInvalidNameThrowsIllegalArgumentException()
            throws Exception
    {
        try
        {
            m_resource.getArchetype("test");
            fail("Exception expected...");
        }
        catch(AnnotatedIllegalArgumentException e)
        {
        }
    }

    @Test
    public void getArchetypeWithUnknownArchetypeIDThrowsNotFoundException()
            throws Exception
    {
        try
        {
            m_resource.getArchetype("openEHR-EHR-COMPOSITION.non_existent_composition.v1");
            fail("Exception expected...");
        }
        catch(NotFoundException e)
        {
        }
    }

}

