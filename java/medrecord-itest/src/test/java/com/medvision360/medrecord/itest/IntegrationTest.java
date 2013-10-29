package com.medvision360.medrecord.itest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.basex.BaseXLocatableStore;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.engine.UIDGenerator;
import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.memstore.MemLocatableStore;
import com.medvision360.medrecord.pv.PVSerializer;
import com.medvision360.medrecord.riio.RIXmlConverter;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.CompositeStore;
import com.medvision360.medrecord.spi.CompositeTransformer;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.LocatableTransformer;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.tck.RMTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.core.Context;
import org.openehr.am.archetype.Archetype;
import org.openehr.build.RMObjectBuilder;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.measurement.SimpleMeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.terminology.SimpleTerminologyService;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@SuppressWarnings("FieldCanBeLocal")
public class IntegrationTest extends RMTestBase
{
    private final static Log log = LogFactory.getLog(IntegrationTest.class);

    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;

    private ArchetypeStore m_archetypeStore;
    private ResourcePatternResolver m_resolver;
    private String m_archetypeLoaderBasePath;
    private boolean m_adlMissingLanguageCompatible;
    private boolean m_adlEmptyPurposeCompatible;
    private ArchetypeLoader m_archetypeLoader;

    private StringGenerator m_stringGenerator;
    private RandomSupport m_randomSupport;
    private AssertionSupport m_assertionSupport;
    private ValueGenerator m_valueGenerator;
    private RMAdapter m_rmAdapter;
    private RMObjectBuilder m_rmObjectBuilder;

    private LocatableGenerator m_locatableGenerator;

    RIXmlConverter m_xmlConverter;

    PVSerializer m_pvSerializer;

    LocatableTransformer m_locatableTransformer;

    List<Context> m_baseXContexts;
    String m_locatableStoreName = "IntegrationTest";
    String m_locatableStorePath = "itest";
    LocatableStore m_locatableStore;
    LocatableStore m_fallbackStore;

    int generated = 0;
    int creationFailed = 0;
    int creationUnsupported = 0;
    int inserted = 0;

    int totalIDs = 0;
    int retrieved = 0;
    int serializeFailed = 0;
    int internalUIDs = 0;
    int serialized = 0;


    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        m_terminologyService = SimpleTerminologyService.getInstance();
        m_measurementService = SimpleMeasurementService.getInstance();

        m_archetypeStore = new MemArchetypeStore();

        m_resolver = new PathMatchingResourcePatternResolver();
        m_archetypeLoaderBasePath = "archetypes";
        m_adlEmptyPurposeCompatible = true;
        m_adlMissingLanguageCompatible = true;
        m_archetypeLoader = new ArchetypeLoader(m_archetypeStore, m_resolver, m_archetypeLoaderBasePath,
                m_adlMissingLanguageCompatible, m_adlEmptyPurposeCompatible);

        m_randomSupport = new RandomSupport();
        m_assertionSupport = new AssertionSupport();
        m_stringGenerator = new StringGenerator();
        m_valueGenerator = new ValueGenerator(m_randomSupport, m_stringGenerator, m_terminologyService,
                m_measurementService);
        m_rmAdapter = new RMAdapter(m_valueGenerator);

        Map<SystemValue, Object> systemValues = new HashMap<>();
        systemValues.put(SystemValue.TERMINOLOGY_SERVICE, m_terminologyService);
        systemValues.put(SystemValue.MEASUREMENT_SERVICE, m_measurementService);
        systemValues.put(SystemValue.LANGUAGE, Terminology.L_en);
        systemValues.put(SystemValue.CHARSET, Terminology.CHARSET_UTF8);
        systemValues.put(SystemValue.TERRITORY, Terminology.C_NL);
        systemValues.put(SystemValue.ENCODING, Terminology.CHARSET_UTF8);
        m_rmObjectBuilder = new RMObjectBuilder(systemValues);

        m_locatableGenerator = new LocatableGenerator(m_archetypeStore, m_randomSupport, m_assertionSupport,
                m_valueGenerator, m_rmAdapter, m_rmObjectBuilder);

        m_xmlConverter = new RIXmlConverter(m_terminologyService, m_measurementService,
                Terminology.CHARSET_UTF8, Terminology.L_en);

        m_pvSerializer = new PVSerializer();

        m_locatableTransformer = getLocatableTransformer();

        m_locatableStore = getLocatableStore();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        for (Context c : m_baseXContexts)
        {
            c.close();
        }
    }

    protected LocatableStore getLocatableStore() throws Exception
    {
        m_baseXContexts = new ArrayList<>();
        CompositeStore store = new CompositeStore(m_locatableStoreName);

        LocatableSelector basicEHR = LocatableSelectorBuilder
                .start()
                        //.requireRMVersion("1.0.2")
                .requireRMName("EHR")
                .matchRMEntity("^(?:COMPOSITION|EHRSTATUS|ACTION|ADMIN_ENTRY|EVALUATION|INSTRUCTION|OBSERVATION)$")
                .build();
        store.addDelegate(getStore("IntegrationTestEHR", basicEHR));

        LocatableSelector basicDemographics = LocatableSelectorBuilder
                .start()
                        //.requireRMVersion("1.0.2")
                .requireRMName("DEMOGRAPHIC")
                .matchRMEntity("^(?:PARTY_IDENTITY|PARTY_RELATIONSHIP|PERSON|ORGANISATION|ROLE|ADDRESS|CAPABILITY)$")
                .build();
        store.addDelegate(getStore("IntegrationTestDEMOGRAPHIC", basicDemographics));

        LocatableSelector xmlFallback = LocatableSelectorBuilder.any();
        store.addDelegate(getStore("IntegrationTestFALLBACK", xmlFallback));

        m_fallbackStore = new MemLocatableStore("IntegrationTestMEMORY");

        store.addDelegate(m_fallbackStore);

        store.clear();
        store.initialize();

        return store;
    }

    protected LocatableStore getStore(String name, LocatableSelector locatableSelector)
    {
        Context c = new Context();
        m_baseXContexts.add(c);

        return new BaseXLocatableStore(
                c,
                m_xmlConverter,
                m_xmlConverter,
                locatableSelector,
                name,
                m_locatableStorePath
        );
    }

    protected LocatableTransformer getLocatableTransformer()
    {
        CompositeTransformer transformer = new CompositeTransformer();
        transformer.addDelegate(new UIDGenerator());
        return transformer;
    }

    public void testEverything() throws Exception
    {
        m_archetypeLoader.loadAll("openehr");
        m_archetypeLoader.loadAll("medfit");
        m_archetypeLoader.loadAll("chiron");
        m_archetypeLoader.loadAll("mobiguide");

        Iterable<ArchetypeID> allArchetypeIDs = m_archetypeStore.list();
        TreeSet<String> sortedIDs = new TreeSet<>();
        Iterables.addAll(sortedIDs, Iterables.transform(allArchetypeIDs, new Function<ArchetypeID, String>()
        {
            public String apply(ArchetypeID input)
            {
                return input.getValue();
            }
        }));
        OUTER:
        for (String archetypeName : sortedIDs)
        {
            ArchetypeID archetypeID = new ArchetypeID(archetypeName);
            Archetype archetype = m_archetypeStore.get(archetypeID);
            if (!supported(archetypeID))
            {
                log.debug(String.format("Skipping skeleton generation for archetype %s, it is not supported",
                        archetypeName));
                continue;
            }

            // generate
            log.debug(String.format("Generating instance of %s", archetypeName));
            Locatable instance;
            try
            {
                instance = m_locatableGenerator.generate(archetype);
                generated++;
            }
            catch (Exception e)
            {
                String message = e.getMessage();
                if (message != null && message.contains("No archetypes found to match the slot"))
                {
                    // this happens a lot: the constraints in the archetypes often don't quite match reality
                    creationUnsupported++;
                    continue;
                }
                message = e.getMessage();
                if (message != null && message.contains("empty items"))
                {
                    // this also happens a lot: there are lists that need slotted entries and then we cannot find a
                    // compatible slotted archetype so we end up with an empty list that's not allowed to be empty
                    creationUnsupported++;
                    continue;
                }
                Throwable cause = e.getCause();
                while (cause != null)
                {
                    message = cause.getMessage();
                    if (message != null && message.contains("empty items"))
                    {
                        creationUnsupported++;
                        continue OUTER;
                    }
                    cause = cause.getCause();
                }
                log.error(String.format("FAILED generating instance of %s: %s", archetypeName, e.getMessage()), e);
                creationFailed++;
                //throw e;
                continue;
            }
            String className = instance.getClass().getSimpleName();
            log.debug(String.format("Got %s for archetype %s", className, archetypeName));

            // transform
            log.debug(String.format("Transforming instance of %s", archetypeName));
            m_locatableTransformer.transform(instance);

            // insert
            m_locatableStore.insert(instance);
            log.debug(String.format("Inserted a %s", archetypeName));
            inserted++;
        }

        int storedInMemory = Iterables.size(m_fallbackStore.list());

        creationFailed += serializeAll();

        report(allArchetypeIDs, storedInMemory);

        assertEquals("No failures", 0, creationFailed);
    }

    private void report(Iterable<ArchetypeID> allArchetypeIDs, int storedInMemory)
    {
        log.info(String.format("Created %s instances using skeleton generation (skipped %s, failed %s, " +
                "unsupported %s)",
                generated, Iterables.size(allArchetypeIDs) - generated, creationFailed,
                creationUnsupported));
        log.info(String.format("Inserted %s locatables (%s in xml databases)",
                inserted, inserted - storedInMemory));
        log.info(String.format("Serialized %s instances (total %s, retrieved %s, failed %s, internal %s)",
                serialized, totalIDs, retrieved, serializeFailed, internalUIDs));
    }

    private int serializeAll() throws IOException
    {
        Iterable<HierObjectID> allIDs = m_locatableStore.list();
        File base = new File("build" + File.separatorChar + "ser");
        if (!base.exists())
        {
            base.mkdirs();
        }
        for (HierObjectID hierObjectID : allIDs)
        {
            totalIDs++;
            String id = null;
            try
            {
                id = hierObjectID.root().getValue();
                Locatable locatable = m_locatableStore.get(hierObjectID);
                retrieved++;
                File target = new File(base, id + ".json");
                FileOutputStream fis = new FileOutputStream(target);
                BufferedOutputStream bos = new BufferedOutputStream(fis);
                m_pvSerializer.serialize(locatable, bos);
                serialized++;
            }
            catch (NotFoundException e)
            {
                internalUIDs++;
            }
            catch (IOException | ParseException | SerializeException e)
            {
                serializeFailed++;
                log.error(String.format("Difficulty serializing %s: %s", id, e.getMessage())); //, e);
            }
        }
        return serializeFailed;
    }

    private String[] skipArchetypes = new String[] {
            // No archetypes found to match the slot at ..., but a value is required
//            "openEHR-DEMOGRAPHIC-ORGANISATION.organisation.v1",
//            "openEHR-DEMOGRAPHIC-PERSON.person-patient.v1",
//            "openEHR-DEMOGRAPHIC-PERSON.person.v1",
//            "openEHR-DEMOGRAPHIC-ROLE.healthcare_consumer.v1",
//            "openEHR-DEMOGRAPHIC-ROLE.healthcare_provider_organisation.v1",
//            "openEHR-DEMOGRAPHIC-ROLE.individual_provider.v1",
    };

    {
        Arrays.sort(skipArchetypes);
    }

    private boolean supported(ArchetypeID archetypeID)
    {
        String archetypeName = archetypeID.getValue();
        if (Arrays.binarySearch(skipArchetypes, archetypeName) < 0)
        {
            return true;
        }

        return false;
    }
}
