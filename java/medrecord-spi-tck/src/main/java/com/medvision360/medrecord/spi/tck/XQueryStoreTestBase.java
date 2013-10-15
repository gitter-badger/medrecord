package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.XQueryStore;

public abstract class XQueryStoreTestBase extends LocatableStoreTestBase {
    protected XQueryStore xQueryStore;

    @Override
    public void setUp()
            throws Exception {
        super.setUp();
        
        xQueryStore = (XQueryStore) store;
    }

    public void testQueryNullArgumentsThrowNPE() throws Exception {
        try {
            xQueryStore.list(null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            xQueryStore.query("<result/>", null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}
    }
}
