/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.LocatableStore;


public abstract class LocatableStoreTestBase extends RMTestBase
{
    protected LocatableStore store;

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();
        store = getStore();
        store.clear();
        store.initialize();

        if (store.supportsTransactions())
        {
            store.begin();
        }
    }

    @Override
    public void tearDown()
            throws Exception
    {
        super.tearDown();
        if (store.supportsTransactions())
        {
            store.rollback();
        }
    }

    protected abstract LocatableStore getStore()
            throws Exception;
}
