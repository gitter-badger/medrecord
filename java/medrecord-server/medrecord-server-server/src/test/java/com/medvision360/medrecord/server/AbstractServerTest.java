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
package com.medvision360.medrecord.server;

import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.lib.common.converter.ExtendedJacksonConverter;
import com.medvision360.medrecord.api.archetype.ArchetypeRequest;
import com.medvision360.medrecord.client.archetype.ArchetypeListResource;
import com.medvision360.medrecord.client.ehr.EHRListResource;
import com.medvision360.medrecord.client.test.TestClearResource;
import com.medvision360.medrecord.client.test.TestClearResourceClearParams;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.tck.RMTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openehr.rm.support.identification.ArchetypeID;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class AbstractServerTest extends RMTestBase
{
    protected static ClientResourceConfig m_resourceConfig;
    protected ArchetypeListResource m_archetypeListResource;
    protected EHRListResource m_ehrListResource;
    
    protected ArchetypeStore m_archetypeStore;
    protected ArchetypeLoader m_archetypeLoader;
    protected ResourcePatternResolver m_resolver;
    protected String m_archetypeLoaderBasePath;
    protected boolean m_adlMissingLanguageCompatible;
    protected boolean m_adlEmptyPurposeCompatible;

    @BeforeClass
    public static void baseClassSetUp() throws Exception
    {
        SLF4JBridgeHandler.install();
        // unfortunately, this messes with the log category name
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");

        ExtendedJacksonConverter.register();
        //noinspection SpellCheckingInspection
        String serviceURL = System.getProperty("integrationtest.service.url");
        if (serviceURL == null || serviceURL.isEmpty())
        {
            //noinspection SpellCheckingInspection
            throw new IllegalStateException("Missing system property integrationtest.service.url");
        }
        m_resourceConfig = new ClientResourceConfig(
               serviceURL + "/v2"
           );
        
        clear();
    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        m_archetypeStore = new MemArchetypeStore();
        m_resolver = new PathMatchingResourcePatternResolver();
        m_archetypeLoaderBasePath = "archetypes";
        m_adlEmptyPurposeCompatible = true;
        m_adlMissingLanguageCompatible = true;
        m_archetypeLoader = new ArchetypeLoader(m_archetypeStore, m_resolver, m_archetypeLoaderBasePath,
                m_adlMissingLanguageCompatible, m_adlEmptyPurposeCompatible);

        m_archetypeListResource = new ArchetypeListResource(m_resourceConfig);
        m_ehrListResource = new EHRListResource(m_resourceConfig);
    }
    
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    protected static void clear() throws Exception
    {
        if (!m_resourceConfig.getBaseUrl().contains("://localhost"))
        {
            throw new IllegalStateException(String.format(
                    "Trying to clear %s, which is not localhost", m_resourceConfig.getBaseUrl()));
        }
        
        TestClearResource resource = new TestClearResource(m_resourceConfig);
        TestClearResourceClearParams params = new TestClearResourceClearParams();
        params.setQueryArgument("confirm", "CONFIRM");
        resource.clear(params);
    }
    
    protected ArchetypeRequest loadArchetypeRequest(String collection, String archetypeName) throws Exception
    {
        ArchetypeID archetypeID = new ArchetypeID(archetypeName);
        m_archetypeLoader.load(collection, archetypeName);
        WrappedArchetype archetype = m_archetypeStore.get(archetypeID);

        ArchetypeRequest request = new ArchetypeRequest();
        request.setAdl(archetype.getAsString());
        return request;
    }

    protected ArchetypeRequest loadArchetypeRequest(String archetypeName) throws Exception
    {
        return loadArchetypeRequest("openehr", archetypeName);
    }
    
    protected void ensureArchetype(String collection, String archetypeName) throws Exception
    {
        ArchetypeRequest request;
        request = loadArchetypeRequest(collection, archetypeName);
        try
        {
            m_archetypeListResource.postArchetype(request);
        }
        catch (DuplicateException e)
        {
            // ok
        }
    }

    protected void ensureArchetype(String archetypeName) throws Exception
    {
        ensureArchetype("openehr", archetypeName);
    }
}
