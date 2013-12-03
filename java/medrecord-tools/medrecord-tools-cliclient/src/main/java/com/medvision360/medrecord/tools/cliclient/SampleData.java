package com.medvision360.medrecord.tools.cliclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.pv.PVSerializer;
import com.medvision360.medrecord.spi.Terminology;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.generic.PartyIdentified;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.EventContext;
import org.openehr.rm.composition.content.ContentItem;
import org.openehr.rm.composition.content.entry.AdminEntry;
import org.openehr.rm.datastructure.itemstructure.ItemList;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.datastructure.itemstructure.representation.Element;
import org.openehr.rm.datatypes.quantity.datetime.DvDate;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.datatypes.text.DvCodedText;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.support.identification.TerminologyID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.terminology.SimpleTerminologyService;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleData extends RestletUtil
{
    protected static final Logger log = LoggerFactory.getLogger(ArchetypeUploader.class);

    /**
     * Helper that uses PVSerializer to turn openehr reference model objects into path/value json.
     * 
     * @param locatable the object to serialize
     * @return a serialized {@link org.restlet.representation.Representation} ready for uploading with restlet
     */
    protected Representation toJsonRequest(Locatable locatable) throws IOException, SerializeException
    {
        PVSerializer pvSerializer = new PVSerializer();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        pvSerializer.serialize(locatable, os, "UTF-8"); // if you don't specify it, UTF-8 is the default
        byte[] serialized = os.toByteArray();
        log.debug("Locatable serialized to path/value:");
        log.debug("----");
        log.debug(new String(serialized, "UTF-8"));
        log.debug("----");

        return toJsonRequest(serialized);
    }

    protected HierObjectID makeUID()
    {
        return new HierObjectID(makeUUID());
    }

    protected String makeUUID()
    {
        return UUID.randomUUID().toString();
    }

    protected EHRStatus makeEHRStatus()
    {
        ItemStructure otherDetails = list("EHRStatus details");
        Archetyped arch = new Archetyped(new ArchetypeID(EHRSTATUS_ARCHETYPE), "1.0.2");
        EHRStatus status = new EHRStatus(makeUID(), "at0001", text("EHR Status (from SampleData.java)"),
                        arch, null, null, null, subject(), true, true, otherDetails);
        return status;
    }

    protected Composition makeComposition()
    {
        return makeComposition(makeUID());
    }
    protected Composition makeComposition(UIDBasedID uid)
    {
        Archetyped archetypeDetails;
        
        archetypeDetails = new Archetyped(
                new ArchetypeID(COMPOSITION_ARCHETYPE),
                "1.0.2");
        List<ContentItem> contentItems = new ArrayList<>();
        Composition composition = new Composition(uid, "at0001", new DvText("composition (From SampleData.java)"),
                archetypeDetails, null, null, null, contentItems, LANGUAGE, context(), provider(), CATEGORY_EVENT, 
                TERRITORY, TERMINOLOGY_SERVICE);
        
        archetypeDetails = new Archetyped(
                new ArchetypeID(ADMIN_ENTRY_ARCHETYPE),
                "1.0.2");
        List<Element> items = new ArrayList<>();
        items.add(new Element(("at0004"), "header", new DvText("date")));
        items.add(new Element(("at0005"), "value", new DvDate("2008-05-17")));
        ItemList itemList = new ItemList("at0003", "item list", items);
        AdminEntry adminEntry = new AdminEntry(makeUID(), "at0002", new DvText("admin entry 1"),
                archetypeDetails, null, null, composition, LANGUAGE, ENCODING,
                subject(), provider(), null, null, itemList, TERMINOLOGY_SERVICE);
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
    
    protected EventContext context()
    {
        DvCodedText setting = new DvCodedText("primary medical", LANGUAGE, ENCODING,
                Terminology.SETTING_primary_medical, TERMINOLOGY_SERVICE);
        return new EventContext(null, time("2013-11-23T12:00:09"), null, null,
                null, setting, null, TERMINOLOGY_SERVICE);
    }

    protected PartySelf subject()
    {
        PartyRef party = new PartyRef(new HierObjectID("1.2.4.5.6.12.1"),
                "PARTY");
        return new PartySelf(party);
    }

    protected PartyIdentified provider()
    {
        PartyRef performer = new PartyRef(new HierObjectID("1.3.3.1.2.42.1"),
                "ORGANISATION");
        return new PartyIdentified(performer, "provider's name", null);
    }

    protected ItemList list(String name)
    {
        String[] nodeIds = {"at1001", "at1002", "at1003"};
        String[] names = {"field 1", "field 2", "field 3"};
        String[] values = {"value 1", "value 2", "value 3"};
        String[] codes = {"code1", "code2", "code3"};
        List<Element> items = new ArrayList<>();
        for (int i = 0; i < names.length; i++)
        {
            items.add(element(nodeIds[i], names[i], values[i], codes[i]));
        }
        return new ItemList("at0100", text(name), items);
    }

    protected Element element(String archetypeNodeId, String name,
            String value, String code)
    {
        return new Element(archetypeNodeId, text(name), codedText(value, code));
    }

    protected DvDateTime time(String time)
    {
        return new DvDateTime(time);
    }

    protected DvText text(String value)
    {
        return new DvText(value);
    }

    protected DvCodedText codedText(String value, String code)
    {
        CodePhrase codePhrase = new CodePhrase(new TerminologyID("SNOMED CT"),
                code);
        return new DvCodedText(value, codePhrase);
    }

    public final static String COMPOSITION_ARCHETYPE = "unittest-EHR-COMPOSITION.composition.v1";
    public final static String ADMIN_ENTRY_ARCHETYPE = "unittest-EHR-ADMIN_ENTRY.date.v2";
    public final static String EHRSTATUS_ARCHETYPE = "unittest-EHR-EHRSTATUS.ehrstatus.v1";
    
    protected final static CodePhrase ENCODING = Terminology.CHARSET_UTF8;
    protected final static CodePhrase LANGUAGE = Terminology.L_en;
    protected final static CodePhrase TERRITORY = Terminology.C_NL;
    protected final static TerminologyService TERMINOLOGY_SERVICE = SimpleTerminologyService.getInstance();
    protected final static DvCodedText CATEGORY_EVENT = new DvCodedText("event",
                LANGUAGE, ENCODING, Terminology.CATEGORY_event,
                TERMINOLOGY_SERVICE);
}
