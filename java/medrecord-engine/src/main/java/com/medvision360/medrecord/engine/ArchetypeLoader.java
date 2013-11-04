package com.medvision360.medrecord.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.am.archetype.Archetype;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import se.acode.openehr.parser.ADLParser;

public class ArchetypeLoader
{
    private final static Log log = LogFactory.getLog(ArchetypeLoader.class);

    private ResourcePatternResolver m_resolver;
    private ArchetypeStore m_store;
    private String m_basePath;
    private boolean m_adlMissingLanguageCompatible;
    private boolean m_adlEmptyPurposeCompatible;
    
    public ArchetypeLoader(ArchetypeStore store, ResourcePatternResolver resolver, String basePath,
            boolean adlMissingLanguageCompatible, boolean adlEmptyPurposeCompatible) throws IOException
    {
        m_store = store;
        m_store.initialize();
        m_resolver = resolver;
        m_basePath = basePath;
        m_adlMissingLanguageCompatible = adlMissingLanguageCompatible;
        m_adlEmptyPurposeCompatible = adlEmptyPurposeCompatible;
    }

    public ArchetypeLoader() throws IOException
    {
        this(new MemArchetypeStore(), new PathMatchingResourcePatternResolver(), "archetypes", true, true);
    }

    public void loadAll(String collection) throws IOException, ParseException
    {
        loadAll(collection, true);
    }

    public void loadAll(String collection, boolean skipErrors) throws IOException, ParseException
    {
        String pattern = String.format("classpath:%s/%s/**/*.adl", m_basePath, collection);
        Resource[] resources = m_resolver.getResources(pattern);
        if (log.isInfoEnabled())
        {
            log.info(String.format("Found %s archetypes for pattern %s", resources.length, pattern));
        }
        for (int i = 0; i < resources.length; i++)
        {
            try
            {
                load(resources[i]);
            }
            catch (IOException|ParseException e)
            {
                    log.error(String.format("Error loading archetype %s: %s",
                            resources[i].getFilename(), e.getMessage()));
                if (!skipErrors)
                {
                    throw e;
                }
            }
        }
    }

    private void load(Resource resource) throws IOException, ParseException
    {
        InputStream is = resource.getInputStream();
        load(resource.getFilename().replaceFirst("\\.adl$", ""), is);
    }

    private void load(String name, InputStream is) throws ParseException, IOException
    {
        try
        {
            load(name, new InputStreamReader(is, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ParseException(e);
        }
    }
        
    private void load(String name, Reader reader) throws ParseException, IOException
    {
        String source = IOUtils.toString(reader);
        ADLParser parser = new ADLParser(source, m_adlMissingLanguageCompatible, 
                    m_adlEmptyPurposeCompatible);
        
        try
        {
            Archetype archetype = parser.parse();
            log.debug(String.format("Loaded archetype %s", archetype.getArchetypeId().getValue()));
            WrappedArchetype wrappedArchetype = new WrappedArchetype(source, archetype);
            m_store.insert(wrappedArchetype);
            log.debug(String.format("Inserted archetype %s", archetype.getArchetypeId().getValue()));
        }
        catch (DuplicateException e)
        {
            // ignore
        }
        catch (Exception e)
        {
            String message = String.format("Error loading archetype %s: %s", name, e.getMessage());
            log.error(message);
            throw new ParseException(message, e);
        }
    }
}
