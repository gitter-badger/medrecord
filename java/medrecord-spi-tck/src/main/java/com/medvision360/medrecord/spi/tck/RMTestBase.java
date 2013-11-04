package com.medvision360.medrecord.spi.tck;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.io.input.BOMInputStream;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.VersionTreeID;
import se.acode.openehr.parser.ADLParser;

public class RMTestBase extends CompositionTestBase
{
    protected EHRStatus m_parent;
    protected PartySelf subject;

    public RMTestBase()
    {
        super(null);
    }

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();

        subject = subject();
        ItemStructure otherDetails = list("EHRStatus details");
        Archetyped arch = new Archetyped(new ArchetypeID("unittest-EHR-EHRSTATUS.ehrstatus.v1"), "1.0.2");
        m_parent = new EHRStatus(makeUID(), "at0001", text("EHR Status"),
                arch, null, null, null, subject, true, true, otherDetails);
    }
    
    protected void assertEqualish(Locatable orig, Locatable other)
    {
        assertEquals(orig.getUid(), other.getUid());
        assertEquals(orig.getArchetypeNodeId(), other.getArchetypeNodeId());
        assertEquals(orig.getName().getValue(), other.getName().getValue());
        assertEquals(orig.getArchetypeDetails().getArchetypeId().getValue(),
                other.getArchetypeDetails().getArchetypeId().getValue());
    }
    
    protected void assertEqualish(Archetype orig, Archetype other)
    {
        assertEquals(orig.getAdlVersion(), other.getAdlVersion());
        assertEquals(orig.getArchetypeId(), other.getArchetypeId());
        assertEquals(orig.getDescription(), other.getDescription());
        SortedSet<String> origPaths = new TreeSet<>(orig.getPathNodeMap().keySet());
        SortedSet<String> otherPaths = new TreeSet<>(other.getPathNodeMap().keySet());
        assertEquals(origPaths.size(), otherPaths.size());
        Iterator<String> origIt = origPaths.iterator();
        Iterator<String> otherIt = otherPaths.iterator();
        while (origIt.hasNext())
        {
            String origPath = origIt.next();
            String otherPath = otherIt.next();
            assertEquals(origPath, otherPath);
        }
    }

    protected void assertEqualish(EHR orig, EHR other)
    {
        assertEquals(orig.getEhrID(), other.getEhrID());
    }

    protected HierObjectID makeUID()
    {
        return new HierObjectID(makeUUID());
    }

    protected String makeUUID()
    {
        return UUID.randomUUID().toString();
    }

    protected ObjectVersionID makeOVID(HierObjectID hierObjectID)
    {
        return new ObjectVersionID(hierObjectID.root(), new HierObjectID("medrecord.spi.tck"), new VersionTreeID("1"));
    }
    
    protected EHR makeEHR() throws Exception
    {
        HierObjectID systemID = makeUID();
        HierObjectID ehrID = makeUID();
        DvDateTime now = new DvDateTime();
        List<ObjectRef> emptyList = new ArrayList<>();
        HierObjectID ehrStatusUid = makeUID();
        ObjectRef ehrStatusRef = new ObjectRef(ehrStatusUid, "unittest", "EHR_STATUS");
        HierObjectID directoryUid = makeUID();
        ObjectRef directoryRef = new ObjectRef(directoryUid, "unittest", "VERSIONED_FOLDER");
        
        EHR ehr = new EHR(systemID, ehrID, now, emptyList, ehrStatusRef, directoryRef, emptyList);
        return ehr;
    }

    protected Locatable makeLocatable() throws Exception
    {
        return makeLocatable(makeUID(), m_parent);
    }
    protected Locatable makeLocatable(UIDBasedID uid, Pathable parent) throws Exception
    {
        Archetyped archetypeDetails;
        
        archetypeDetails = new Archetyped(
                new ArchetypeID("unittest-EHR-COMPOSITION.composition.v1"),
                "1.0.2");
        DvCodedText category = TestCodeSetAccess.EVENT;
        // todo composition cannot have a parent, but then how do we link EHR/EHR_STATUS? Hmm?
        List<ContentItem> contentItems = new ArrayList<>();
        Composition composition = new Composition(uid, "at0001", new DvText("composition"),
                archetypeDetails, null, null, null, contentItems, lang, context(), provider(), category, 
                territory(), ts);
        
        archetypeDetails = new Archetyped(
                new ArchetypeID("unittest-EHR-ADMIN_ENTRY.date.v2"),
                "1.0.2");
        List<Element> items = new ArrayList<>();
        items.add(new Element(("at0004"), "header", new DvText("date")));
        items.add(new Element(("at0005"), "value", new DvDate("2008-05-17")));
        ItemList itemList = new ItemList("at0003", "item list", items);
        AdminEntry adminEntry = new AdminEntry(makeUID(), "at0002", new DvText("admin entry 1"),
                archetypeDetails, null, null, composition, lang, encoding,
                subject(), provider(), null, null, itemList, ts);
//        AdminEntry adminEntry2 = new AdminEntry(makeUID(), "at0002", new DvText("admin entry 2"),
//                archetypeDetails, null, null, composition, lang, encoding,
//                subject(), provider(), null, null, itemList, ts);
//        AdminEntry adminEntry3 = new AdminEntry(makeUID(), "at0002", new DvText("admin entry 3"),
//                archetypeDetails, null, null, composition, lang, encoding,
//                subject(), provider(), null, null, itemList, ts);
        // adminEntry.set("/data[at0002]/items[at0004]/value", new DvDate("2009-06-18"));
        // note: set() does not support item indices...
        //   adminEntry.set("/data[at0002]/items[2]/value", new DvDate("2009-07-19"));

        contentItems.add(adminEntry);
//        contentItems.add(adminEntry2);
//        contentItems.add(adminEntry3);
        
        return composition;
    }
    
    protected Archetype loadArchetype() throws Exception
    {
        return loadArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.adl");
    }
    
    protected Archetype loadArchetype(String archetypeId) throws Exception
    {
        InputStream is = getClass().getResourceAsStream("/" + archetypeId);
        final ADLParser parser = new ADLParser(
                new BOMInputStream(is),
                false,
                false
        );

        final Archetype archetype = parser.parse();
        return archetype;
    }
}
