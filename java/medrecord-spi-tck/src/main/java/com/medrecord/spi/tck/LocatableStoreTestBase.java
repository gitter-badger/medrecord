package com.medrecord.spi.tck;

import com.medrecord.spi.LocatableStore;
import com.medrecord.spi.exceptions.DuplicateException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.CompositionTestBase;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;

import java.util.UUID;

public abstract class LocatableStoreTestBase extends CompositionTestBase {
    protected LocatableStore store;
    protected String idRoot = "locatable.example.com";
    protected EHRStatus parent;
    protected PartySelf subject;
    
    public LocatableStoreTestBase() {
        super(null);
    }

    public LocatableStoreTestBase(String test) {
        super(test);
    }

    @Override
    public void setUp()
            throws Exception {
        super.setUp();
        store = getStore();
        store.clear();
        store.initialize();
        
        subject = subject();
        ItemStructure otherDetails = list("EHRStatus details");
        Archetyped arch = new Archetyped(new ArchetypeID("openehr-unittest-status.EHRSTATUS.v1"), "1.4");
        parent = new EHRStatus(makeUID(), "at0001", text("EHR Status"),
                arch, null, null, null, subject, true, true, otherDetails);
        if (store.supportsTransactions()) {
            store.begin();
        }
    }

    @Override
    public void tearDown()
            throws Exception {
        super.tearDown();
        if (store.supportsTransactions()) {
            store.rollback();
        }
    }

    public void testBasicCRUD() throws Exception {
        HierObjectID uid = new HierObjectID(idRoot, makeUUID());
        Locatable orig = makeLocatable(uid, parent);
        
        assertFalse(store.has(uid));
        
        Locatable inserted = store.insert(orig);
        assertEqualish(orig, inserted);
        try {
            store.insert(orig);
            fail("Should not allow inserting the same locatable twice");
        } catch(DuplicateException e) {}

        assertTrue(store.has(uid));

        Locatable retrieved = store.get(uid);
        assertEqualish(orig, retrieved);
        
        Locatable modify = makeLocatable(uid, parent);
        modify.set("/name", "modified name");
        Locatable modified = store.update(modify);
        
        assertTrue(store.has(uid));
        
        store.delete(uid);

        assertFalse(store.has(uid));       
    }
    
    public void testNullArguments() throws Exception {
        try {
            store.insert(null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.has((HierObjectID)null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.has((ObjectVersionID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.get((HierObjectID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.get((ObjectVersionID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.update(null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.delete((HierObjectID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.delete((ObjectVersionID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

    }
    
    private void assertEqualish(Locatable orig, Locatable other) {
        assertEquals(orig.getUid(), other.getUid());
        assertEquals(orig.getArchetypeNodeId(), other.getArchetypeNodeId());
        assertEquals(orig.getName(), other.getName());
        assertEquals(orig.getArchetypeDetails(), other.getArchetypeDetails());
    }

    public HierObjectID makeUID() {
        return new HierObjectID(idRoot, makeUUID());
    }
    
    public String makeUUID() {
        return UUID.randomUUID().toString();
    }

    protected abstract LocatableStore getStore()
            throws Exception;

    protected abstract Locatable makeLocatable(UIDBasedID uid, Pathable parent)
            throws Exception;

}
