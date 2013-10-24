package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.basex.BaseXLocatableStore;
import com.medvision360.medrecord.engine.ArchetypeLoader;
import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.riio.RIXmlConverter;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.CompositeStore;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.RMTestBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.core.Context;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.util.SkeletonGenerator;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@SuppressWarnings("FieldCanBeLocal")
public class IntegrationTest extends RMTestBase
{
    private final static Log log = LogFactory.getLog(IntegrationTest.class);

    private ArchetypeStore m_archetypeStore;
    private ResourcePatternResolver m_resolver;
    private String m_archetypeLoaderBasePath;
    private boolean m_adlMissingLanguageCompatible;
    private boolean m_adlEmptyPurposeCompatible;
    private ArchetypeLoader m_archetypeLoader;
    
    //private GenerationStrategy m_generationStrategy;
    private SkeletonGenerator m_skeletonGenerator;
    
    RIXmlConverter m_xmlConverter;

    List<Context> m_baseXContexts;
    String m_locatableStoreName = "IntegrationTest";
    String m_locatableStorePath = "itest";
    LocatableStore m_locatableStore;
    
    @Override
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

        // todo experiment with strategies m_generationStrategy = GenerationStrategy.MAXIMUM;
        m_skeletonGenerator = SkeletonGenerator.getInstance(); // todo make configurable
        
        m_xmlConverter = new RIXmlConverter(ts, ms, encoding, lang);
        
        m_baseXContexts = new ArrayList<>();
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
        CompositeStore store = new CompositeStore(m_locatableStoreName);

        LocatableSelector basicEHR = LocatableSelectorBuilder
                .start()
                .requireRMVersion("1.0.2")
                .requireRMName("EHR")
                .matchRMEntity("^(?:COMPOSITION|EHRSTATUS|ACTION|ADMIN_ENTRY|EVALUATION|INSTRUCTION|OBSERVATION)$")
                .build();
        store.addDelegate(getStore("IntegrationTestEHR", basicEHR));

        LocatableSelector basicDemographics = LocatableSelectorBuilder
                .start()
                .requireRMVersion("1.0.2")
                .requireRMName("DEMOGRAPHIC")
                .matchRMEntity("^(?:PARTY_IDENTITY|PARTY_RELATIONSHIP|PERSON|ORGANISATION|ROLE)$")
                .build();
        store.addDelegate(getStore("IntegrationTestDEMOGRAPHIC", basicDemographics));

        LocatableSelector fallback = LocatableSelectorBuilder.any();
        store.addDelegate(getStore("IntegrationTestFALLBACK", fallback));
        
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
    
    public void testEverything() throws Exception
    {
        m_archetypeLoader.loadAll("openehr");
        m_archetypeLoader.loadAll("medfit");
        m_archetypeLoader.loadAll("chiron");
        m_archetypeLoader.loadAll("mobiguide");
        
        Iterable<ArchetypeID> allArchetypeIDs = m_archetypeStore.list();
        int generated = 0;
        int inserted = 0;
        for (ArchetypeID archetypeID : allArchetypeIDs)
        {
            String archetypeName = archetypeID.getValue();
            Archetype archetype = m_archetypeStore.get(archetypeID);
            if (!supported(archetypeID))
            {
                log.debug(String.format("Skipping skeleton generation for archetype %s, it is not supported",
                        archetypeName));
                continue;
            }
            
            
            log.debug(String.format("Generating instance of %s", archetypeName));
            Object instance;
            try
            {
                instance = m_skeletonGenerator.create(archetype);
                generated++;
            }
            catch (Exception e)
            {
                log.error(String.format("FAILED generating instance of %s: %s", archetypeName, e.getMessage()));
                throw e;
            }
            String className = instance.getClass().getSimpleName();
            log.debug(String.format("Got %s for archetype %s", className, archetypeName));
            
            
            if (instance instanceof Locatable)
            {
                Locatable locatable = (Locatable)instance;
                m_locatableStore.insert(locatable);
                log.debug(String.format("Inserted a %s", archetypeName));
                inserted++;
            }
        }
        log.info(String.format("Created %s instances using skeleton generation (skipped %s)",
                generated, Iterables.size(allArchetypeIDs)-generated));
        log.info(String.format("Inserted %s locatables (skipped %s non-locatables)",
                inserted, generated - inserted));
    }
    
    private String[] skipConcepts = new String[] {
            // Failed to construct: ITEM_TREE, value for attribute 'items' has wrong type,
            // expected "interface java.util.List", but got
            // "class org.openehr.rm.datastructure.itemstructure.representation.Element"
            // "class org.openehr.rm.datastructure.itemstructure.representation.Cluster
            "address",
            "person_identifier",
            "person_details",
            //"person",

            "mobiguide_vmr_clinical_statement_encounter_event",
            "mobiguide_vmr_clinical_statement_appointment_request",
            "mobiguide_vmr_clinical_statement_substance_administration_order",
            "mobiguide_vmr_clinical_statement_undelivered_substance_administration",
            "mobiguide_vmr_clinical_statement_substance_administration_event",
            "mobiguide_vmr_clinical_statement_scheduled_appointment",
            "mobiguide_vmr_clinical_statement_denied_adverse_event",
            "mobiguide_vmr_clinical_statement_missed_appointment",
            "mobiguide_vmr_clinical_statement_substance_dispensation_event",
            "mobiguide_vmr_clinical_statement_observation_order",
            "mobiguide_vmr_clinical_statement_problem",
            "mobiguide_vmr_clinical_statement_substance_administration_proposal",
            "mobiguide_vmr_clinical_statement_appointement_proposal",
            "mobiguide_vmr_clinical_statement_goal",
            
            // failed to create new instance of  HISTORY with valueMap: 
            "mobiguide_vmr_clinical_statement_denied_problem",
            "mobiguide_vmr_clinical_statement_goal_proposal",
            "mobiguide_vmr_clinical_statement_observation_proposal",
            "mobiguide_vmr_clinical_statement_procedure_event",
            "mobiguide_vmr_clinical_statement_unconducted_observation",
            "mobiguide_vmr_clinical_statement_adverse_event",
            "sweating",
            "implantable_devices",
            "CHF_aetiology",
            "hourly_energy_expenditure",
            "NYHA_classification",
            "indirect_oximetry",
            "daily_activity",
            "daily_weight_change",
            "monitoring_body_mass_index",
            "monitoring_temperature",
            "monitoring_heartrate",
            "waist_to_hip_ratio",
            "left_ventricle",
            "environmental_variables",
            
            // missing value for required attribute "id" of type class org.openehr.rm.support.identification.ObjectID
            // while constructing class org.openehr.rm.support.identification.PartyRef with valueMap:
            // {template_id=null}
            "medvision_ehrstatus",
            
            // missing value for required attribute "uid" of type class
            // org.openehr.rm.support.identification.UIDBasedID while constructing class
            // org.openehr.rm.demographic.Person with valueMap
            "mobiguide_vmr_person_evaluated_person",
            "fifo_user",
            "healthcare_provider_organisation",
            "healthcare_consumer",

            // missing value for required attribute "value" of type class java.lang.String while constructing class
            // org.openehr.rm.datatypes.basic.DvBoolean with valueMap: {template_id=null}
            "individual_credentials",
            
            // missing value for required attribute "description" of type class
            // org.openehr.rm.datastructure.itemstructure.ItemStructure while constructing class
            // org.openehr.rm.composition.content.entry.Activity with valueMap
            "imaging",
            
            // missing value for required attribute "addresses" of type interface java.util.List while constructing
            // class org.openehr.rm.demographic.Contact with valueMap
            "person",
            "individual_provider",
            "organisation",
            
            // missing value for required attribute "value" of type class java.lang.String while constructing class
            // org.openehr.rm.datatypes.basic.DvBoolean with valueMap:
            "person_name",
            "medvision_person",
            "chiron_person",
            
            // term of given code: at0000, language: en not found..
            "fifo_progress",
            "physical_activity_progress",
            
            // no child object..
            "fifo_activity",
            "medvision_party_relationship_person_person",
            "physical_activity",
            "chiron_party_relationship_person_person",
            
            // unknown original language openEHR::en
            "monitoring_blood_pressure"
    };
    {
        Arrays.sort(skipConcepts);
    }

    private boolean supported(ArchetypeID archetypeID)
    {
        String conceptName = archetypeID.conceptName();
        if (Arrays.binarySearch(skipConcepts, conceptName) < 0)
        {
            return true;
        }
        
        return false;
    }
}
