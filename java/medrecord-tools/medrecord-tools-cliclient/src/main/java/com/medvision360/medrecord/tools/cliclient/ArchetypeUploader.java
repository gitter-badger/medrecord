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
package com.medvision360.medrecord.tools.cliclient;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.spi.ArchetypeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@SuppressWarnings("FieldCanBeLocal")
public class ArchetypeUploader
{
    private static final Logger log = LoggerFactory.getLogger(ArchetypeUploader.class);

    private ArchetypeStore m_archetypeStore;
    private ArchetypeLoader m_archetypeLoader;
    
    public ArchetypeUploader(String baseUrl) throws IOException
    {
        m_archetypeStore = new RemoteArchetypeStore("RemoteArchetypeStore", baseUrl);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String archetypeLoaderBasePath = "archetypes";
        boolean adlEmptyPurposeCompatible = true;
        boolean adlMissingLanguageCompatible = true;
        m_archetypeLoader = new ArchetypeLoader(m_archetypeStore, resolver, archetypeLoaderBasePath,
                adlMissingLanguageCompatible, adlEmptyPurposeCompatible);
    }

    public static void main(String[] args) throws Exception
    {
        SLF4JBridgeHandler.install();
        log.debug("Upload starting");
        
        String baseUrl = System.getProperty("medrecord.url", "http://medrecord.test.medvision360.org/medrecord");
        if (baseUrl.endsWith("/"))
        {
            baseUrl = baseUrl.substring(0, baseUrl.length()-1);
        }
        if (baseUrl.endsWith("/v2"))
        {
            baseUrl = baseUrl.substring(0, baseUrl.length()-3);
        }
        
        // unfortunately, this messes with the log category name
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");

        ArchetypeUploader instance = new ArchetypeUploader(baseUrl);
        instance.loadArchetypes();
    }

    private void loadArchetypes() throws IOException, ParseException
    {
        m_archetypeLoader.loadAll("openehr");
        //m_archetypeLoader.loadAll("medfit");
        //m_archetypeLoader.loadAll("chiron");
        //m_archetypeLoader.loadAll("mobiguide");
    }
}
