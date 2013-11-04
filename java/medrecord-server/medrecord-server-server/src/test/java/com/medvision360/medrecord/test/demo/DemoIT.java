/** @file
 * This file belongs to the MEDvision360 Identity Server Integration
 * Tests and contains the OrganizationIT class.
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.test.demo;

import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.lib.common.converter.ExtendedJacksonConverter;
import com.medvision360.medrecord.api.demo.MrDemoResult;
import com.medvision360.medrecord.client.demo.MrDemoResource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DemoIT
{
    final ClientResourceConfig m_resourceConfig = new ClientResourceConfig(
        System.getProperty("integrationtest.service.url")
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
}
