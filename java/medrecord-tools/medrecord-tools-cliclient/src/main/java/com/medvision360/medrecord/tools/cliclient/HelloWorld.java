package com.medvision360.medrecord.tools.cliclient;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.client.ehr.EHRListResource;
import com.medvision360.medrecord.client.ehr.EHRLocatableListResource;
import com.medvision360.medrecord.client.query.XQueryResource;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.spi.ArchetypeStore;
import org.apache.commons.io.IOUtils;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.ehr.EHRStatus;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import static com.google.common.base.Preconditions.checkNotNull;

public class HelloWorld extends SampleData
{
    protected static final Logger log = LoggerFactory.getLogger(ArchetypeUploader.class);

    protected ArchetypeLoader m_archetypeLoader;
    
    public HelloWorld(String baseUrl) throws IOException
    {
        ArchetypeStore archetypeStore = new RemoteArchetypeStore("RemoteArchetypeStore", baseUrl);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String archetypeLoaderBasePath = "archetypes";
        boolean adlEmptyPurposeCompatible = true;
        boolean adlMissingLanguageCompatible = true;
        m_archetypeLoader = new ArchetypeLoader(archetypeStore, resolver, archetypeLoaderBasePath,
                adlMissingLanguageCompatible, adlEmptyPurposeCompatible);
    }

    public static void main(String[] args) throws Exception
    {
        setupLogging();
        log.debug("Hello World starting");
        String baseUrl = getBaseUrl();
        HelloWorld instance = new HelloWorld(baseUrl);
        
        /// Here we go!
        
        log.debug("first, make sure we have the archetypes we use on the server");
        instance.loadArchetypes();
        
        String ehrID;
        String compositionID;
        
        log.debug("creating a new EHR");
        // this is an example of using the openehr reference model in SampleData to make some content, 
        // but you don't have to do that -- just use path/value if you want -- see below
        ehrID = instance.createEHRUsingReferenceModel(baseUrl);
        log.debug("Created new EHR with ID " + ehrID);
        
        log.debug("creating a new Composition");
        // this is an example of using the openehr reference model in SampleData to make some content,
        // but you don't have to do that -- just use path/value if you want -- see below
        compositionID = instance.createCompositionUsingReferenceModel(baseUrl, ehrID);
        log.debug("Created new Composition, with ID " + compositionID);
        
        log.debug("creating another new EHR");
        ehrID = instance.createEHRFromPathValue(baseUrl, "/ehr_example.json");
        log.debug("Created new EHR with ID " + ehrID);

        log.debug("creating another new Composition");
        compositionID = instance.createCompositionFromPathValue(baseUrl, ehrID, "/composition_example.json");
        log.debug("Created another new Composition, with ID " + compositionID);
        
        log.debug("Doing an example XQuery on the new data");
        instance.listArchetypesInUse(baseUrl);
    }

    protected static void setupLogging()
    {
        SLF4JBridgeHandler.install();
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
    }

    protected static String getBaseUrl()
    {
        String baseUrl = System.getProperty("medrecord.url", "http://medrecord.test.medvision360.org/medrecord");
        if (baseUrl.endsWith("/"))
        {
            baseUrl = baseUrl.substring(0, baseUrl.length()-1);
        }
        if (baseUrl.endsWith("/v2"))
        {
            baseUrl = baseUrl.substring(0, baseUrl.length()-3);
        }
        return baseUrl;
    }

    protected void loadArchetypes() throws IOException, ParseException
    {
        m_archetypeLoader.loadAll("unittest");
    }
    
    protected String createEHRUsingReferenceModel(String baseUrl) throws IOException, RecordException
    {
        // generate some sample data
        EHRStatus ehrStatus = makeEHRStatus();
        Representation request = toJsonRequest(ehrStatus);

        // initialize a restlet client for /v2/ehr
        ClientResourceConfig resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        EHRListResource ehrListResource = new EHRListResource(resourceConfig);
        
        // do POST /v2/ehr
        ID id = ehrListResource.postEHR(request);
        String idString = id.getId();
        return idString;
    }

    protected String createCompositionUsingReferenceModel(String baseUrl, String ehrID) throws IOException, RecordException
    {
        // generate some sample data
        Composition composition = makeComposition();
        Representation request = toJsonRequest(composition);

        // initialize a restlet client for /v2/ehr/{id}/locatable
        ClientResourceConfig resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        EHRLocatableListResource locatableListResource = new EHRLocatableListResource(resourceConfig, ehrID);

        // do POST /v2/ehr/{id}/locatable
        ID id = locatableListResource.postLocatable(request);
        String idString = id.getId();
        return idString;
    }

    protected String createEHRFromPathValue(String baseUrl, String fileName) throws IOException, RecordException
    {
        // get some sample data from the filesystem
        InputStream is = this.getClass().getResourceAsStream(fileName);
        String json = IOUtils.toString(is);
        Representation request = toJsonRequest(json);

        // initialize a restlet client for /v2/ehr
        ClientResourceConfig resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        EHRListResource ehrListResource = new EHRListResource(resourceConfig);
        
        // do POST /v2/ehr
        ID id = ehrListResource.postEHR(request);
        String idString = id.getId();
        return idString;
    }

    protected String createCompositionFromPathValue(String baseUrl, String ehrID, String fileName)
            throws IOException, RecordException
    {
        // get some sample data from the filesystem
        InputStream is = this.getClass().getResourceAsStream(fileName);
        String json = IOUtils.toString(is);
        Representation request = toJsonRequest(json);

        // initialize a restlet client for /v2/ehr
        ClientResourceConfig resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        EHRLocatableListResource locatableListResource = new EHRLocatableListResource(resourceConfig, ehrID);

        // do POST /v2/ehr/{id}/locatable
        ID id = locatableListResource.postLocatable(request);
        String idString = id.getId();
        return idString;
    }

    protected void listArchetypesInUse(String baseUrl) throws IOException, RecordException
    {
        // initialize a restlet client for /v2/query/xquery
        ClientResourceConfig resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        XQueryResource xQueryResource = new XQueryResource(resourceConfig);
        
        // simple example xQuery
        String q = "(: find all archetypes that are in use :)\n" +
                "distinct-values(//archetype_details/archetype_id/value/text()[concat(., ' ')])";
        
        // do the query
        Representation representation = xQueryResource.xQuery(q);
        
        // print the results
        InputStream is = representation.getStream();
        String body = IOUtils.toString(is);
        log.debug("xquery result:");
        log.debug("----");
        log.debug(body);
        log.debug("----");
    }
}
