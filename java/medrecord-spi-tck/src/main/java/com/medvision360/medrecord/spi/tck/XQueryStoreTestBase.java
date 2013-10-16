/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.XQueryStore;

public abstract class XQueryStoreTestBase extends LocatableStoreTCKTestBase {
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
