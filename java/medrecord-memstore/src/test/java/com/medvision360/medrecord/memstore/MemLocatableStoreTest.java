package com.medvision360.medrecord.memstore;

import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTCKTestBase;

public class MemLocatableStoreTest extends LocatableStoreTCKTestBase
{
    @Override
    protected LocatableStore getStore()
            throws Exception
    {
        return new MemLocatableStore();
    }
}
