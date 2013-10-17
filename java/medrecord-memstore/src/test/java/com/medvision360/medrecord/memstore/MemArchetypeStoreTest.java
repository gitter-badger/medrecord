package com.medvision360.medrecord.memstore;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.tck.ArchetypeStoreTCKTestBase;

public class MemArchetypeStoreTest extends ArchetypeStoreTCKTestBase
{
    @Override
    protected ArchetypeStore getStore() throws Exception
    {
        return new MemArchetypeStore();
    }
}
