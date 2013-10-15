package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.XQueryStoreTestBase;

public class NoopStoreTest extends XQueryStoreTestBase {
    // this test is mostly here to just check the TCK 'wiring' is ok

    @Override
    protected LocatableStore getStore() throws Exception {
        return new NoopStore();
    }

    @Override
    public void testBasicCRUD()
            throws Exception {
        try {
            super.testBasicCRUD();
        } catch(UnsupportedOperationException e) {}
    }

    @Override
    public void testBasicNullArgumentsThrowNPE()
            throws Exception {
        try {
            super.testBasicNullArgumentsThrowNPE();
        } catch(UnsupportedOperationException e) {}
    }

    @Override
    public void testQueryNullArgumentsThrowNPE()
            throws Exception {
        try {
            super.testQueryNullArgumentsThrowNPE();
        } catch(UnsupportedOperationException e) {}
    }

    @Override
    public void testBasicVersioning()
            throws Exception {
        try {
            super.testBasicVersioning();
        } catch(UnsupportedOperationException e) {}
    }

    @Override
    public void testUnknownIDThrowsNotFound()
            throws Exception {
        try {
            super.testUnknownIDThrowsNotFound();
        } catch(UnsupportedOperationException e) {}
    }
}
