package com.medvision360.medrecord.itest;

import java.util.Map;

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
import com.medvision360.medrecord.spi.tck.RMTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehr.build.SystemValue;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class AbstractIntegrationTest extends RMTestBase
{
    protected final static Log log = LogFactory.getLog(AbstractIntegrationTest.class);
    protected MedRecordEngine m_engine;
    protected TerminologyService m_terminologyService;
    protected MeasurementService m_measurementService;
    protected ArchetypeStore m_archetypeStore;
    protected ResourcePatternResolver m_resolver;
    protected String m_archetypeLoaderBasePath;
    protected boolean m_adlMissingLanguageCompatible;
    protected boolean m_adlEmptyPurposeCompatible;
    protected ArchetypeLoader m_archetypeLoader;
    protected StringGenerator m_stringGenerator;
    protected RandomSupport m_randomSupport;
    protected AssertionSupport m_assertionSupport;
    protected ValueGenerator m_valueGenerator;
    protected RMAdapter m_rmAdapter;
    protected LocatableGenerator m_locatableGenerator;
    protected LocatableSerializer m_pvSerializer;
    protected String m_locatableStoreName = "IntegrationTest";
    protected LocatableStore m_locatableStore;
    protected LocatableStore m_fallbackStore;
    protected LocatableValidator m_locatableValidator;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        m_engine = new MedRecordEngine();
        m_engine.setName("IntegrationTest");
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
}
