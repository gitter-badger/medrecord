package com.medvision360.medrecord.pv;

import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.support.measurement.TestMeasurementService;
import org.openehr.rm.support.terminology.TestTerminologyService;

public class PVConverterTest extends LocatableConverterTCKTestBase
{
    protected ArchetypeStore m_archetypeStore;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        m_archetypeStore = new MemArchetypeStore();
        m_archetypeStore.initialize();
        Archetype archetype = loadArchetype();
        m_archetypeStore.insert(archetype);
    }

    @Override
    protected LocatableParser getParser() throws Exception
    {
        return new PVParser(new TestTerminologyService(), new TestMeasurementService(), encoding, lang);
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new PVSerializer();
    }
}
