package com.medvision360.medrecord.spi.tck;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

public abstract class LocatableStoreTCKTestBase extends LocatableStoreTestBase
{
    public void testTransactions() throws Exception
    {
        store.supportsTransactions();
        store.commit(); // commits the tx from setup
        store.begin();
        store.rollback();
        store.begin(); // open tx for tearDown to clean up
    }

    public void testStatus() throws Exception
    {
        store.verifyStatus();
        String report = store.reportStatus();
        assertNotNull(report);
        assertFalse("".equals(report));
    }

    public void testName() throws Exception
    {
        String name = store.getName();
        assertNotNull(name);
        assertFalse("".equals(name));
    }

    public void testSupport() throws Exception
    {
        Locatable locatable = makeLocatable();
        assertTrue(store.supports(locatable));
        assertTrue(store.supports(locatable.getArchetypeDetails()));

        Locatable unsupported = makeUnsupportedLocatable();
        if (unsupported != null)
        {
            assertFalse(store.supports(unsupported));
        }

        Archetyped unsupportedArchetyped = makeUnsupportedArchetyped();
        if (unsupportedArchetyped != null)
        {
            assertFalse(store.supports(unsupportedArchetyped));
        }
    }

    public void testBasicCRUD() throws Exception
    {
        HierObjectID uid = new HierObjectID(makeUUID());
        Locatable orig = makeLocatable(uid, m_parent);

        assertFalse(store.has(uid));

        Iterable<HierObjectID> hierObjectIDs = store.list();
        assertEquals(0, Iterables.size(hierObjectIDs));

        Locatable inserted = store.insert(orig);
        assertEqualish(orig, inserted);
        try
        {
            store.insert(orig);
            fail("Should not allow inserting the same locatable twice");
        }
        catch (DuplicateException e)
        {
        }

        assertTrue(store.has(uid));

        hierObjectIDs = store.list();
        assertEquals(1, Iterables.size(hierObjectIDs));

        Locatable retrieved = store.get(uid);
        assertEqualish(orig, retrieved);

        Locatable modify = makeLocatable(uid, m_parent);
        modify.set("/name/value", "modified name");
        Locatable modified = store.update(modify);
        assertEqualish(modify, modified);
        Locatable modifiedThenRetrieved = store.get(uid);
        assertEqualish(modify, modifiedThenRetrieved);
        hierObjectIDs = store.list();
        assertEquals(1, Iterables.size(hierObjectIDs));

        try
        {
            HierObjectID otherUid = new HierObjectID(makeUUID());
            Locatable other = makeLocatable(otherUid, m_parent);
            store.update(other);
            fail("Should not allow updating non-existent locatable");
        }
        catch (NotFoundException e)
        {
        }

        assertTrue(store.has(uid));

        store.delete(uid);
        assertFalse(store.has(uid));
        hierObjectIDs = store.list();
        assertEquals(0, Iterables.size(hierObjectIDs));
        // how many versions to expect here is probably a bit implementation-dependent...
    }
    
    public void testEHRSupport() throws Exception
    {
        Iterable<HierObjectID> hierObjectIDs;
        EHR EHR = makeEHR();
        
        try
        {
            store.list(EHR);
            fail("Should not allow listing non-existent EHR");
        }
        catch (NotFoundException e)
        {
        }
        
        HierObjectID uid = new HierObjectID(makeUUID());
        Locatable orig = makeLocatable(uid, m_parent);
        
        Locatable inserted = store.insert(EHR, orig);
        assertEqualish(orig, inserted);
        
        hierObjectIDs = store.list(EHR);
        assertEquals(1, Iterables.size(hierObjectIDs));
        assertEquals(inserted.getUid(), Iterables.getFirst(hierObjectIDs, null));

        hierObjectIDs = store.list(EHR, "COMPOSITION");
        assertEquals(1, Iterables.size(hierObjectIDs));

        hierObjectIDs = store.list(EHR, "PERSON");
        assertEquals(0, Iterables.size(hierObjectIDs));
    }

    public void testBasicNullArgumentsThrowNPE() throws Exception
    {
        try
        {
            store.get(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            store.insert(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            store.update(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            store.delete(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            store.has(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            store.list(null);
            fail("Null argument should throw NPE");
        }
        catch (NullPointerException e)
        {
        }
    }

    public void testUnknownIDThrowsNotFound() throws Exception
    {
        HierObjectID hierObjectID = makeUID();

        try
        {
            store.get(hierObjectID);
            fail("Unknown id should throw NFE");
        }
        catch (NotFoundException e)
        {
        }

        try
        {
            store.delete(hierObjectID);
            fail("Unknown id should throw NFE");
        }
        catch (NotFoundException e)
        {
        }
    }

    public void testInitializeCanBeCalledMultipleTimes() throws Exception
    {
        store.initialize();
        store.initialize();
        store.initialize();
    }

    public void testClearCanBeCalledMultipleTimes() throws Exception
    {
        store.clear();
        store.initialize();
        store.clear();
        store.initialize();
        store.clear();
    }

    protected Locatable makeUnsupportedLocatable() throws Exception
    {
        return null;
    }

    protected Archetyped makeUnsupportedArchetyped() throws Exception
    {
        return null;
    }
}
