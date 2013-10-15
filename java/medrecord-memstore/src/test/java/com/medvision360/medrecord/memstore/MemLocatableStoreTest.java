package com.medvision360.medrecord.memstore;

import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTestBase;

public class MemLocatableStoreTest extends LocatableStoreTestBase {
    @Override
    protected LocatableStore getStore()
            throws Exception {
        return new MemLocatableStore();
    }
}
