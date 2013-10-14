package com.medrecord.memstore;

import com.medrecord.spi.LocatableStore;
import com.medrecord.spi.tck.LocatableStoreTestBase;

public class MemLocatableStoreTest extends LocatableStoreTestBase {
    @Override
    protected LocatableStore getStore()
            throws Exception {
        return new MemLocatableStore();
    }
}
