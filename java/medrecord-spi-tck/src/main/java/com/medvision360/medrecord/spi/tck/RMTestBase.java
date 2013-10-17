package com.medvision360.medrecord.spi.tck;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.composition.CompositionTestBase;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.identification.VersionTreeID;

public class RMTestBase extends CompositionTestBase
{
    public RMTestBase()
    {
        super(null);
    }

    protected void assertEqualish(Locatable orig, Locatable other)
    {
        assertEquals(orig.getUid(), other.getUid());
        assertEquals(orig.getArchetypeNodeId(), other.getArchetypeNodeId());
        assertEquals(orig.getName(), other.getName());
        assertEquals(orig.getArchetypeDetails(), other.getArchetypeDetails());
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

    protected Locatable makeLocatable(UIDBasedID uid, Pathable parent) throws Exception
    {
        Archetyped archetypeDetails = new Archetyped(
                new ArchetypeID("unittest-EHR-ADMIN_ENTRY.date.v2"),
                "1.4");
        List<Element> items = new ArrayList<>();
        items.add(new Element(("at0001"), "header", new DvText("date")));
        items.add(new Element(("at0002"), "value", new DvDate("2008-05-17")));
        ItemList itemList = new ItemList("at0003", "item list", items);
        AdminEntry adminEntry = new AdminEntry(uid, "at0004", new DvText("admin entry"),
                archetypeDetails, null, null, parent, lang, encoding,
                subject(), provider(), null, null, itemList, ts);
        return adminEntry;
    }

}
