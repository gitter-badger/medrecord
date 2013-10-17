package com.medvision360.medrecord.pv;

import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;
import org.openehr.am.archetype.Archetype;

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
        return new PVParser(m_archetypeStore);
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new PVSerializer();
    }
}
