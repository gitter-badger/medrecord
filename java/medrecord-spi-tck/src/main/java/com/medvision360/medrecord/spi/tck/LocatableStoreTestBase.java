/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.LocatableStore;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;


public abstract class LocatableStoreTestBase extends RMTestBase
{
    protected LocatableStore store;
    protected EHRStatus parent;
    protected PartySelf subject;

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();
        store = getStore();
        store.clear();
        store.initialize();

        subject = subject();
        ItemStructure otherDetails = list("EHRStatus details");
        Archetyped arch = new Archetyped(new ArchetypeID("unittest-EHR-EHRSTATUS.ehrstatus.v1"), "1.4");
        parent = new EHRStatus(makeUID(), "at0001", text("EHR Status"),
                arch, null, null, null, subject, true, true, otherDetails);
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
