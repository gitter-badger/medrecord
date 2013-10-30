package com.medvision360.medrecord.basex;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.spi.CompositeStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTestBase;
import org.basex.core.Context;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;

public class MultiDatabaseTest extends LocatableStoreTestBase
{
    List<Context> ctx;
    CompositeStore store;

    @Override
    public void setUp() throws Exception
    {
        ctx = new ArrayList<>();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        for (Context c : ctx)
        {
            c.close();
        }
    }

    LocatableParser parser = new MockLocatableParser();
    LocatableSerializer serializer = new MockLocatableSerializer();
    String name = "MultiDatabaseTest";
    String path = "unittest";

    protected LocatableStore getStore() throws Exception
    {
        store = new CompositeStore(name);

        LocatableSelector specialistECG = LocatableSelectorBuilder
                .start()
                .requireRMVersion("1.0.2")
                .matchArchetypeId("^openEHR-EHR-OBSERVATION.ecg.v[12](?:draft)?$")
                .build();
        store.addDelegate(getStore("MultiDatabaseTestECG", specialistECG));

        LocatableSelector basicEHR = LocatableSelectorBuilder
                .start()
                .requireRMVersion("1.0.2")
                .requireRMName("EHR")
                .matchRMEntity("^(?:COMPOSITION|EHRSTATUS|ACTION|ADMIN_ENTRY|EVALUATION|INSTRUCTION|OBSERVATION)$")
                .build();
        store.addDelegate(getStore("MultiDatabaseTestEHR", basicEHR));

        LocatableSelector basicDemographics = LocatableSelectorBuilder
                .start()
                .requireRMVersion("1.0.2")
                .requireRMName("DEMOGRAPHIC")
                .matchRMEntity("^(?:PARTY_IDENTITY|PARTY_RELATIONSHIP|PERSON|ORGANISATION|ROLE)$")
                .build();
        store.addDelegate(getStore("MultiDatabaseTestDEMOGRAPHIC", basicDemographics));

        LocatableSelector standardsOnly = LocatableSelectorBuilder
                .start()
                .matchRMVersion("^1\\.0\\.[012]$")
                .matchRMOriginator("openEHR")
                .build();
        store.addDelegate(getStore("MultiDatabaseTestOPENEHR", standardsOnly));

        LocatableSelector fallback = LocatableSelectorBuilder.any();
        store.addDelegate(getStore("MultiDatabaseTestFALLBACK", fallback));

        return store;
    }

    protected LocatableStore getStore(String name, LocatableSelector locatableSelector)
    {
        Context c = new Context();
        ctx.add(c);

        return new BaseXLocatableStore(
                c,
                parser,
                serializer,
                locatableSelector,
                name,
                path
        );
    }

    public void testUsingCompositeStoreWithMultipleBaseXDatabaseBackends() throws Exception
    {
        Archetyped personType = new Archetyped(new ArchetypeID("unittest-DEMOGRAPHIC-PERSON.person.v1"), "1.0.2");
        Archetyped ehrStatusType = new Archetyped(new ArchetypeID("unittest-EHR-EHRSTATUS.ehrstatus.v1"), "1.0.2");
        Archetyped ecgType = new Archetyped(new ArchetypeID("openEHR-EHR-OBSERVATION.ecg.v1"), "1.0.2");
        Archetyped medicationListType = new Archetyped(new ArchetypeID("unittest-EHR-COMPOSITION.medication_list.v1"),
                "1.0.2");
        Archetyped adminEntryType = new Archetyped(new ArchetypeID("unittest-EHR-ADMIN_ENTRY.status.v1"),
                "1.0.2");

        assertTrue(store.supports(personType));
        assertTrue(store.supports(ehrStatusType));
        assertTrue(store.supports(ecgType));
        assertTrue(store.supports(medicationListType));
        assertTrue(store.supports(adminEntryType));

        Locatable person = makeLocatable(personType, "Jane Doe");
        person = store.insert(person);
        person.set("/name/value", "Jane Doe-GotMarried");
        store.update(person);

        Locatable ehrStatus = makeLocatable(ehrStatusType, "TEST_EHR");
        ehrStatus = store.insert(ehrStatus);
        ehrStatus.set("/name/value", "TEST_EHR_MODIFIED");
        store.update(ehrStatus);

        Locatable ecg = makeLocatable(ecgType, "My First Heart rate");
        ecg = store.insert(ecg);
        ecg.set("/name/value", "My Second Heart rate");
        store.update(ecg);

        Locatable medicationList = makeLocatable(medicationListType, "My asperin addiction");
        medicationList = store.insert(medicationList);
        medicationList.set("/name/value", "I'm off the asperin");
        store.update(medicationList);

        Locatable adminEntry = makeLocatable(adminEntryType, "test approved");
        adminEntry = store.insert(adminEntry);
        adminEntry.set("/name/value", "test finished");
        store.update(adminEntry);


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        store.query("//name/value", os);
        byte[] result = os.toByteArray();
        String resultString = new String(result, "UTF-8");
        assertTrue(resultString.contains("Jane Doe-GotMarried"));
        assertTrue(resultString.contains("TEST_EHR_MODIFIED"));
        assertTrue(resultString.contains("My Second Heart rate"));
        assertTrue(resultString.contains("I'm off the asperin"));
        assertTrue(resultString.contains("test finished"));
    }

    protected Locatable makeLocatable(Archetyped archetypeDetails, String name) throws Exception
    {
        HierObjectID uid = makeUID();
        DvText dvName = new DvText(name);

        Locatable locatable = new MockLocatable(uid, "at0001", dvName, archetypeDetails, null, null, m_parent);
        return locatable;
    }
}
