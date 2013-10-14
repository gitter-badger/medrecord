package com.medrecord.spi.base;

import com.medrecord.spi.LocatableStore;
import com.medrecord.spi.tck.LocatableStoreTestBase;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.UIDBasedID;

import java.util.ArrayList;
import java.util.List;

public class NoopStoreTest extends LocatableStoreTestBase {

    @Override
    protected LocatableStore getStore() throws Exception {
        return new NoopStore();
    }

    @Override
    protected Locatable makeLocatable(UIDBasedID uid, Pathable parent) throws Exception {
        Archetyped archetypeDetails = new Archetyped(
                new ArchetypeID("openehr-unittest-ADMIN_ENTRY.date.v2"),
                "1.4");        
        List<Element> items = new ArrayList<Element>();
        items.add(new Element(("at0001"), "header", new DvText("date")));
        items.add(new Element(("at0002"), "value",	new DvDate("2008-05-17")));
        ItemList itemList = new ItemList("at0003", "item list", items);        
        AdminEntry adminEntry = new AdminEntry(makeUID(), "at0004", new DvText("admin entry"),
        		archetypeDetails, null, null, null, lang, encoding, 
        		subject(), provider(), null, null, itemList, ts);
        return adminEntry;
    }

    @Override
    public void testBasicCRUD()
            throws Exception {
        try {
            super.testBasicCRUD();
        } catch(UnsupportedOperationException e) {}
    }

    @Override
    public void testNullArguments()
            throws Exception {
        try {
            super.testNullArguments();
        } catch(UnsupportedOperationException e) {}
    }
}
