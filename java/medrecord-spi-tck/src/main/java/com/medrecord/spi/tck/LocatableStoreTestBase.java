package com.medrecord.spi.tck;

import com.google.common.collect.Iterables;
import com.medrecord.spi.LocatableStore;
import com.medrecord.spi.exceptions.DuplicateException;
import com.medrecord.spi.exceptions.NotFoundException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.CompositionTestBase;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.VersionTreeID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class LocatableStoreTestBase extends CompositionTestBase {
    protected LocatableStore store;
    protected EHRStatus parent;
    protected PartySelf subject;
    
    public LocatableStoreTestBase() {
        super(null);
    }

    @SuppressWarnings("UnusedDeclaration")
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
    
    public void testTransactions() throws Exception {
        store.supportsTransactions();
        store.commit(); // commits the tx from setup
        store.begin();
        store.rollback();
        store.begin(); // open tx for tearDown to clean up
    }
    
    public void testStatus() throws Exception {
        store.verifyStatus();
        String report = store.reportStatus();
        assertNotNull(report);
        assertFalse("".equals(report));
    }
    
    public void testName() throws Exception {
        String name = store.getName();
        assertNotNull(name);
        assertFalse("".equals(name));
    }
    
    public void testSupport() throws Exception {
        HierObjectID uid = new HierObjectID(makeUUID());
        Locatable locatable = makeLocatable(uid, parent);
        assertTrue(store.supports(locatable));
        assertTrue(store.supports(locatable.getArchetypeDetails()));
        
        Locatable unsupported = makeUnsupportedLocatable();
        if (unsupported != null) {
            assertFalse(store.supports(unsupported));
        }
        
        Archetyped unsupportedArchetyped = makeUnsupportedArchetyped();
        if (unsupportedArchetyped != null) {
            assertFalse(store.supports(unsupportedArchetyped));
        }
    }

    public void testBasicCRUD() throws Exception {
        HierObjectID uid = new HierObjectID(makeUUID());
        Locatable orig = makeLocatable(uid, parent);
        
        assertFalse(store.has(uid));

        Iterable<HierObjectID> hierObjectIDs = store.list();
        assertEquals(0, Iterables.size(hierObjectIDs));
        Iterable<ObjectVersionID> objectVersionIDs = store.listVersions();
        int initialVersionSize = Iterables.size(objectVersionIDs); // don't need this to be 0!
        
        Locatable inserted = store.insert(orig);
        assertEqualish(orig, inserted);
        try {
            store.insert(orig);
            fail("Should not allow inserting the same locatable twice");
        } catch(DuplicateException e) {}

        assertTrue(store.has(uid));
        
        hierObjectIDs = store.list();
        assertEquals(1, Iterables.size(hierObjectIDs));
        objectVersionIDs = store.listVersions();
        int afterInsertVersionSize = Iterables.size(objectVersionIDs);
        assertTrue(afterInsertVersionSize > initialVersionSize); // don't need this to be +1!

        Locatable retrieved = store.get(uid);
        assertEqualish(orig, retrieved);
        
        Locatable modify = makeLocatable(uid, parent);
        modify.set("/name/value", "modified name");
        Locatable modified = store.update(modify);
        assertEqualish(modify, modified);
        Locatable modifiedThenRetrieved = store.get(uid);
        assertEqualish(modify, modifiedThenRetrieved);
        hierObjectIDs = store.list();
        assertEquals(1, Iterables.size(hierObjectIDs));
        objectVersionIDs = store.listVersions();
        int afterUpdateVersionSize = Iterables.size(objectVersionIDs);
        assertTrue(afterUpdateVersionSize > afterInsertVersionSize); // don't need this to be +1!
        
        try {
            HierObjectID otherUid = new HierObjectID(makeUUID());
            Locatable other = makeLocatable(otherUid, parent);
            store.update(other);
            fail("Should not allow updating non-existent locatable");
        } catch(NotFoundException e) {}

        assertTrue(store.has(uid));

        store.delete(uid);
        assertFalse(store.has(uid));       
        hierObjectIDs = store.list();
        assertEquals(0, Iterables.size(hierObjectIDs));
        // how many versions to expect here is probably a bit implementation-dependent...
    }
    
    public void testBasicVersioning() throws Exception {
        HierObjectID uid = new HierObjectID(makeUUID());
        Locatable orig = makeLocatable(uid, parent);

        HierObjectID otherUid = new HierObjectID(makeUUID());
        Locatable other = makeLocatable(otherUid, parent);
        
        store.insert(orig);
        store.insert(other);
        
        Iterable<Locatable> versions = store.getVersions(uid);
        assertTrue(Iterables.size(versions) > 0);
        for (Locatable version : versions) {
            HierObjectID versionUid = (HierObjectID) version.getUid();
            assertEquals(uid, versionUid);
        }
        
        Iterable<ObjectVersionID> objectVersionIDs = store.listVersions();
        assertTrue(Iterables.size(objectVersionIDs) > 0);
        ObjectVersionID ovid = Iterables.getFirst(objectVersionIDs, null);
        assertNotNull(ovid);
        
        assertTrue(store.has(ovid));
        assertTrue(store.hasAny(ovid));
        
        boolean foundOrigVersion = false;
        boolean deletedOrigVersion = false;
        for (ObjectVersionID objectVersionID : objectVersionIDs) {
            Locatable version = store.get(objectVersionID);
            HierObjectID versionUid = (HierObjectID) version.getUid();
            if (uid.equals(versionUid)) {
                foundOrigVersion = true;
                assertEqualish(orig, version);
                if (!deletedOrigVersion) {
                    store.delete(objectVersionID);
                    deletedOrigVersion = true;
                }
            }
        }
        assertTrue(foundOrigVersion);
        assertTrue(deletedOrigVersion);

        HierObjectID hierObjectID = makeUID();
        ObjectVersionID objectVersionID = makeOVID(hierObjectID);
        assertFalse(store.has(objectVersionID));
        assertFalse(store.hasAny(objectVersionID));
    }
    
    public void testBasicNullArgumentsThrowNPE() throws Exception {
        try {
            store.get((HierObjectID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.get((ObjectVersionID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.getVersions(null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.insert(null);
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

        try {
            store.has((HierObjectID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.has((ObjectVersionID) null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}

        try {
            store.hasAny(null);
            fail("Null argument should throw NPE");
        } catch(NullPointerException e) {}
    }
    
    public void testUnknownIDThrowsNotFound() throws Exception {
        HierObjectID hierObjectID = makeUID();
        ObjectVersionID objectVersionID = makeOVID(hierObjectID);
        
        try {
            store.get(hierObjectID);
            fail("Unknown id should throw NFE");
        } catch (NotFoundException e) {}
        
        try {
            store.get(objectVersionID);
            fail("Unknown id should throw NFE");
        } catch (NotFoundException e) {}

        try {
            store.getVersions(hierObjectID);
            fail("Unknown id should throw NFE");
        } catch (NotFoundException e) {}
        
        try {
            store.delete(hierObjectID);
            fail("Unknown id should throw NFE");
        } catch (NotFoundException e) {}
        
        try {
            store.delete(objectVersionID);
            fail("Unknown id should throw NFE");
        } catch (NotFoundException e) {}
    }
    
    public void testInitializeCanBeCalledMultipleTimes() throws Exception {
        store.initialize();
        store.initialize();
        store.initialize();
    }
    
    public void testClearCanBeCalledMultipleTimes() throws Exception {
        store.clear();
        store.initialize();
        store.clear();
        store.initialize();
        store.clear();
    }

    ///
    /// Helpers
    ///
    
    protected void assertEqualish(Locatable orig, Locatable other) {
        assertEquals(orig.getUid(), other.getUid());
        assertEquals(orig.getArchetypeNodeId(), other.getArchetypeNodeId());
        assertEquals(orig.getName(), other.getName());
        assertEquals(orig.getArchetypeDetails(), other.getArchetypeDetails());
    }

    protected HierObjectID makeUID() {
        return new HierObjectID(makeUUID());
    }
    
    protected String makeUUID() {
        return UUID.randomUUID().toString();
    }
    
    protected ObjectVersionID makeOVID(HierObjectID hierObjectID) {
        return new ObjectVersionID(hierObjectID.root(), new HierObjectID("medrecord.spi.tck"), new VersionTreeID("1"));
    }
    
    ///
    /// TCK interface for concrete classes to implement / override
    ///

    protected abstract LocatableStore getStore()
            throws Exception;

    protected Locatable makeLocatable(UIDBasedID uid, Pathable parent) throws Exception {
        Archetyped archetypeDetails = new Archetyped(
                new ArchetypeID("openehr-unittest-ADMIN_ENTRY.date.v2"),
                "1.4");        
        List<Element> items = new ArrayList<>();
        items.add(new Element(("at0001"), "header", new DvText("date")));
        items.add(new Element(("at0002"), "value",	new DvDate("2008-05-17")));
        ItemList itemList = new ItemList("at0003", "item list", items);        
        AdminEntry adminEntry = new AdminEntry(uid, "at0004", new DvText("admin entry"),
        		archetypeDetails, null, null, parent, lang, encoding, 
        		subject(), provider(), null, null, itemList, ts);
        return adminEntry;
    }
    
    protected Locatable makeUnsupportedLocatable() throws Exception {
        return null;
    }

    protected Archetyped makeUnsupportedArchetyped() throws Exception {
        return null;
    }
}
