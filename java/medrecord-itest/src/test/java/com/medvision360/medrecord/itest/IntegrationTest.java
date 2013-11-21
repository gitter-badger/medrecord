package com.medvision360.medrecord.itest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.engine.ArchetypeBasedValidator;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.engine.UIDGenerator;
import com.medvision360.medrecord.memstore.MemLocatableStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.CompositeStore;
import com.medvision360.medrecord.spi.CompositeTransformer;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.spi.TransformingLocatableStore;
import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.api.ValidationResult;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.spi.tck.RMTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.am.archetype.Archetype;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@SuppressWarnings("FieldCanBeLocal")
public class IntegrationTest extends RMTestBase
{
    private final static Log log = LogFactory.getLog(IntegrationTest.class);
    
    public final static Set<String> archetypesToSkip;
    static {
        archetypesToSkip = new HashSet<>();
        archetypesToSkip.addAll(Arrays.asList(
                // recursion requiring an instance of themselves as content...
                "openEHR-EHR-ACTION.follow_up.v1",
                "openEHR-EHR-ACTION.imaging.v1",
                "openEHR-EHR-ACTION.intravenous_fluid_administration.v1",
                
                // requires openEHR-EHR-SECTION.medications.v1 which we don't have
                "openEHR-EHR-COMPOSITION.prescription.v1"
        ));
    }
    
    private MedRecordEngine m_engine;

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

    private LocatableGenerator m_locatableGenerator;

    LocatableSerializer m_pvSerializer;

    String m_locatableStoreName = "IntegrationTest";
    LocatableStore m_locatableStore;
    LocatableStore m_fallbackStore;
    
    LocatableValidator m_locatableValidator;

    int skipped = 0;
    int generated = 0;
    int creationFailed = 0;
    int creationUnsupported = 0;
    int inserted = 0;

    int totalIDs = 0;
    int retrieved = 0;
    int serializeFailed = 0;
    int internalUIDs = 0;
    int serialized = 0;
    
    int validated = 0;
    int valid = 0;
    int invalid = 0;
    int validRule = 0;
    int invalidRule = 0;

    long generatedNs = 0;
    long insertedNs = 0;
    long retrievedNs = 0;
    long serializedNs = 0;
    long validatedNs = 0;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        skipped = 0;
        generated = 0;
        creationFailed = 0;
        creationUnsupported = 0;
        inserted = 0;
    
        totalIDs = 0;
        retrieved = 0;
        serializeFailed = 0;
        internalUIDs = 0;
        serialized = 0;
    
        validated = 0;
        valid = 0;
        invalid = 0;
        validRule = 0;
        invalidRule = 0;

        generatedNs = 0;
        insertedNs = 0;
        retrievedNs = 0;
        serializedNs = 0;
        validatedNs = 0;
        
        m_engine = new MedRecordEngine();
        m_engine.setStoreValidation(false); // so we can split insert-able vs validate-able
        m_engine.initialize();
        
        m_terminologyService = m_engine.getTerminologyService();
        m_measurementService = m_engine.getMeasurementService();
        m_archetypeStore = m_engine.getArchetypeStore();
    
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
        
        Map<SystemValue, Object> systemValues = m_engine.getSystemValues();

        m_locatableGenerator = new LocatableGenerator(m_archetypeStore, m_randomSupport, m_assertionSupport,
                m_valueGenerator, m_rmAdapter, systemValues);

        m_pvSerializer = m_engine.getLocatableSerializer("application/json", null);

        m_locatableStore = getLocatableStore();

        m_locatableValidator = new ArchetypeBasedValidator(m_archetypeStore, systemValues);
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        m_engine.dispose();
    }

    protected LocatableStore getLocatableStore() throws Exception
    {
        CompositeStore store = new CompositeStore(m_locatableStoreName);

        LocatableStore base = m_engine.getLocatableStore();
        store.addDelegate(base);

        MemLocatableStore memStore = new MemLocatableStore("IntegrationTestMEMORY");
        UIDGenerator uidGenerator = new UIDGenerator();
        CompositeTransformer transformer = new CompositeTransformer();
        transformer.addDelegate(uidGenerator);
        TransformingLocatableStore transformingStore = new TransformingLocatableStore("IntegrationTestMEMORY", memStore, 
                transformer);
        m_fallbackStore = transformingStore;

        store.addDelegate(m_fallbackStore);

        store.clear();
        store.initialize();

        return store;
    }

    public void testEverything() throws Exception
    {
        loadAll();

        generateAll();

        serializeAll();

        validateAll();
        
        useEngineApi();

        report();
        
        assertEquals("No failures", 0, creationFailed);
    }

    private void loadAll() throws Exception
    {
        m_archetypeStore.clear();
        m_archetypeLoader.loadAll("openehr");
        m_archetypeLoader.loadAll("medfit");
        m_archetypeLoader.loadAll("chiron");
        m_archetypeLoader.loadAll("mobiguide");
    }

    private void generateAll() throws Exception
    {
        subject = subject();
        Archetyped arch = new Archetyped(new ArchetypeID("openEHR-EHR-EHRSTATUS.medvision_ehrstatus.v1"), "1.0.2");
        EHRStatus status = new EHRStatus(makeUID(), "at0001", text("EHR Status"),
                arch, null, null, null, subject, true, true, null);

        EHR createdEHR = m_engine.createEHR(status);
        EHRStatus retrievedStatus = m_engine.getEHRStatus(createdEHR);
        EHRStatus retrievedByIdStatus = m_engine.getEHRStatus(new HierObjectID(retrievedStatus.getUid().getValue()));
        assertEqualish(retrievedStatus, retrievedByIdStatus);

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
            
            if (archetypesToSkip.contains(archetypeID.getValue()))
            {
                log.info(String.format("Skipping problematic archetype %s", archetypeID.getValue()));
                skipped++;
                continue;
            }
            
            WrappedArchetype wrappedArchetype = m_archetypeStore.get(archetypeID);
            Archetype archetype = wrappedArchetype.getArchetype();
            Locatable generated = generate(archetype);
            insert(createdEHR, generated);
        }
    }

    private Locatable generate(Archetype archetype) throws Exception
    {
        long ct;
        
        String archetypeName = archetype.getArchetypeId().getValue();
        
        // generate
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Generating instance of %s", archetypeName));
        }
        Locatable instance;
        try
        {
            ct = System.nanoTime();
            instance = m_locatableGenerator.generate(archetype);
            generatedNs += System.nanoTime() - ct;

            generated++;
        }
        catch (Exception e)
        {
            String message = e.getMessage();
            log.error(String.format("FAILED generating instance of %s: %s", archetypeName, message));
            creationFailed++;
            return null;
        }
        String className = instance.getClass().getSimpleName();
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Got %s for archetype %s", className, archetypeName));
        }
        
        return instance;
    }
    
    private Locatable insert(EHR EHR, Locatable instance) throws Exception
    {
        long ct = System.nanoTime();
        Locatable insertedInstance = m_locatableStore.insert(EHR, instance);
        insertedNs += System.nanoTime() - ct;

        inserted++;
        if (log.isDebugEnabled())
        {
            log.debug(String.format("Inserted a %s", instance.getArchetypeDetails().getArchetypeId().getValue()));
        }
        
        return insertedInstance;
    }

    private void serializeAll() throws IOException
    {
        Iterable<HierObjectID> allIDs = m_locatableStore.list();
        File base = new File("build" + File.separatorChar + "ser");
        if (!base.exists())
        {
            base.mkdirs();
        }
        for (HierObjectID hierObjectID : allIDs)
        {
            serialize(base, hierObjectID);
        }
    }

    private void serialize(File base, HierObjectID hierObjectID)
    {
        totalIDs++;
        String id = null;
        try
        {
            id = hierObjectID.root().getValue();

            long ct = System.nanoTime();
            Locatable locatable = m_locatableStore.get(hierObjectID);
            retrievedNs += System.nanoTime() - ct;

            retrieved++;
            
            File target = new File(base, id + ".json");
            FileOutputStream fis = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fis, 1024*16);
            
            ct = System.nanoTime();
            m_pvSerializer.serialize(locatable, bos);
            serializedNs += System.nanoTime() - ct;
            
            bos.flush();
            fis.close();

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

    private void validateAll() throws IOException, ParseException, NotSupportedException
    {
        long ct = System.nanoTime();
        for (HierObjectID hierObjectID : m_locatableStore.list())
        {
            Locatable locatable;
            try
            {
                locatable = m_locatableStore.get(hierObjectID);
            }
            catch (NotFoundException e)
            {
                continue;
            }
            validate(locatable);
        }
        validatedNs += System.nanoTime() - ct;
    }

    private void validate(Locatable locatable) throws NotSupportedException
    {
        ValidationReport report = m_locatableValidator.validate(locatable);
        validated++;

        if (!report.isValid())
        {
            System.out.println("=================");
            System.out.println("VALIDATION REPORT");
            System.out.println("=================");
            System.out.println("violations: " + Iterables.size(report.getErrors()));
            System.out.println();
        }
        Iterable<ValidationResult> results = report.getReport();
        for (ValidationResult result : results)
        {
            if (!report.isValid())
            {
                if (!result.isValid())
                {
                    System.out.println(result.getMessage());
                }
            }
            if (result.isValid())
            {
                validRule++;
            }
            else
            {
                invalidRule++;
            }
        }
        if (!report.isValid())
        {
            System.out.println();
            invalid++;
        }
        else
        {
            valid++;
        }
    }
    
    private void useEngineApi() throws Exception
    {
        Iterable<HierObjectID> ehrIDs = m_engine.getEHRStore().list();
        for (HierObjectID ehrID : ehrIDs)
        {
            EHR EHR = m_engine.getEHRStore().get(ehrID);
            Iterable<HierObjectID> locatableIDs;
            try
            {
                locatableIDs = m_engine.getLocatableStore().list(EHR);
            }
            catch(NotFoundException e)
            {
                // ignored
                continue;
            }
            Set<String> rmTypes = new HashSet<>();
            for (HierObjectID locatableID : locatableIDs)
            {
                Locatable locatable = m_engine.getLocatableStore().get(locatableID);
                assertNotNull(locatable);
                rmTypes.add(locatable.getArchetypeDetails().getArchetypeId().rmEntity());
            }
            for (String rmType : rmTypes)
            {
                locatableIDs = m_engine.getLocatableStore().list(EHR, rmType);
                for (HierObjectID locatableID : locatableIDs)
                {
                    Locatable locatable = m_engine.getLocatableStore().get(locatableID);
                    assertNotNull(locatable);
                    String retrievedRmType = locatable.getArchetypeDetails().getArchetypeId().rmEntity();
                    assertEquals(rmType, retrievedRmType);
                }
            }
        }
    }

    private void report() throws IOException
    {
        int storedInMemory = Iterables.size(m_fallbackStore.list());

        log.info(String.format("Created %s instances using skeleton generation (skipped %s, failed %s, " +
                "unsupported %s)",
                generated, skipped, creationFailed, creationUnsupported));
        log.info(String.format("Inserted %s locatables (%s in xml databases)",
                inserted, inserted - storedInMemory));
        log.info(String.format("Serialized %s instances (total %s, retrieved %s, failed %s, internal %s)",
                serialized, totalIDs, retrieved, serializeFailed, internalUIDs));
        log.info(String.format("Validated %s instances (valid %s, invalid %s, valid rules %s, invalid rules %s)",
                validated, valid, invalid, validRule, invalidRule));
        log.info(String.format("generated/s %.2f, inserted/s %.2f, validated/s %.2f, " +
                "retrieved/s %.2f, serialized/s %.2f",
                generated/(generatedNs/10E8),
                inserted/(insertedNs/10E8),
                validated/(validatedNs/10E8),
                retrieved/(retrievedNs/10E8),
                serialized/(serializedNs/10E8)
                ));
    }

}
