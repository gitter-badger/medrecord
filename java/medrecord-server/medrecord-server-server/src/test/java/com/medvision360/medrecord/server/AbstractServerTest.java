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
import org.junit.Before;
import org.junit.BeforeClass;
import org.openehr.rm.support.identification.ArchetypeID;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public abstract class AbstractServerTest
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
        ExtendedJacksonConverter.register();
        //noinspection SpellCheckingInspection
        m_resourceConfig = new ClientResourceConfig(
               System.getProperty("integrationtest.service.url") + "/v2"
           );
        
        clear();
    }

    @Before
    public void baseSetUp() throws Exception
    {
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
    
    protected ArchetypeRequest loadArchetype(String archetypeName) throws Exception
    {
        ArchetypeID archetypeID = new ArchetypeID(archetypeName);
        m_archetypeLoader.load("openehr", archetypeName);
        WrappedArchetype archetype = m_archetypeStore.get(archetypeID);

        ArchetypeRequest request = new ArchetypeRequest();
        request.setAdl(archetype.getAsString());
        return request;
    }

    protected void ensureArchetype(String archetypeName) throws Exception
    {
        ArchetypeRequest request;
        request = loadArchetype(archetypeName);
        try
        {
            m_archetypeListResource.postArchetype(request);
        }
        catch (DuplicateException e)
        {
            // ok
        }
    }
}
