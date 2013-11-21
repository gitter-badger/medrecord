package com.medvision360.medrecord.basex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.spi.DeletableEHR;
import com.medvision360.medrecord.spi.EHRParser;
import com.medvision360.medrecord.spi.EHRSerializer;
import com.medvision360.medrecord.spi.SoftDeletable;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectRef;

public class XmlEHRConverter extends AbstractXmlConverter implements EHRParser, EHRSerializer
{
    @Override
    public EHR parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, "UTF-8");
    }

    @Override
    public EHR parse(InputStream is, String encoding) throws IOException, ParseException
    {
        Document d = toDocument(is, encoding);
        
        HierObjectID systemID = new HierObjectID(xpath(d, "//systemID/value", Filters.element())); 
        HierObjectID ehrID = new HierObjectID(xpath(d, "//ehrID/value", Filters.element())); 
        DvDateTime timeCreated = new DvDateTime(xpath(d, "//timeCreated/value", Filters.element())); 
        List<ObjectRef> contributions = new ArrayList<>();
        
        HierObjectID ehrStatusId = new HierObjectID(xpath(d, "//ehrStatus/id/value", Filters.element()));
        String ehrStatusNamespace = xpath(d, "//ehrStatus/namespace", Filters.element());
        String ehrStatusType = xpath(d, "//ehrStatus/type", Filters.element());
        ObjectRef ehrStatus = new ObjectRef(ehrStatusId, ehrStatusNamespace, ehrStatusType);
        
		ObjectRef directory = null;
		String directoryIdValue = xpath(d, "//directory/id/value", Filters.element());
		if (directoryIdValue != null)
		{
			HierObjectID directoryId = new HierObjectID(directoryIdValue);
			String directoryNamespace = xpath(d, "//directory/namespace", Filters.element());
			String directoryType = xpath(d, "//directory/type", Filters.element());
			directory = new ObjectRef(directoryId, directoryNamespace, directoryType);
		}
        List<ObjectRef> compositions = new ArrayList<>();
        
        boolean deleted = Boolean.parseBoolean(xpath(d, "//deleted", Filters.element())); 

        EHR EHR = new DeletableEHR(systemID, ehrID, timeCreated, contributions, ehrStatus, directory, compositions,
                deleted);
        return EHR;
    }

    @Override
    public void serialize(EHR EHR, OutputStream os) throws IOException, SerializeException
    {
        serialize(EHR, os, "UTF-8");
    }

    @Override
    public void serialize(EHR EHR, OutputStream os, String encoding) throws IOException, SerializeException
    {
        Element root = new Element("EHR");
        set(root, "/systemID/value", EHR.getSystemID().getValue());
        set(root, "/ehrID/value", EHR.getEhrID().getValue());
        set(root, "/timeCreated/value", EHR.getTimeCreated().getValue());

        set(root, "/ehrStatus/id/value", EHR.getEhrStatus().getId().getValue());
        set(root, "/ehrStatus/namespace", EHR.getEhrStatus().getNamespace());
        set(root, "/ehrStatus/type", EHR.getEhrStatus().getType());
        
        ObjectRef directory = EHR.getDirectory();
        if (directory != null)
        {
            set(root, "/directory/id/value", directory.getId().getValue());
            set(root, "/directory/namespace", directory.getNamespace());
            set(root, "/directory/type", directory.getType());
        }
        
        if (EHR instanceof SoftDeletable)
        {
            SoftDeletable deletable = (SoftDeletable) EHR;
            set(root, "/deleted", Boolean.toString(deletable.isDeleted()));
        }
        else
        {
            set(root, "/deleted", Boolean.toString(false));
        }

        Document d = new Document(root);

        outputDocument(d, os, encoding);
    }

    @Override
    public String getFormat()
    {
        return "ehr-xml";
    }
}
