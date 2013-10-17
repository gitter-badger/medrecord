package com.medvision360.medrecord.basex;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.LocatableSerializer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

class MockLocatableSerializer implements LocatableSerializer
{
    @Override
    public void serialize(Locatable locatable, OutputStream os) throws IOException
    {
        serialize(locatable, os, "UTF-8");
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding) throws IOException
    {
        String rmEntity = locatable.getArchetypeDetails().getArchetypeId().rmEntity();
        Element root = new Element(rmEntity);
        root.setAttribute("archetype_node_id", locatable.getArchetypeNodeId());
        
        set(root, "/uid/value", locatable.getUid().getValue());
        set(root, "/archetype_id/value", locatable.getArchetypeDetails().getArchetypeId().getValue());
        set(root, "/name/value", locatable.getName().getValue());
        
        Document d = new Document(root);

        Format format = Format.getPrettyFormat();
        format.setEncoding(encoding);
        XMLOutputter output = new XMLOutputter(format);
        output.output(d, os);
    }

    private void set(Element element, String xpath, String value)
    {
        Element currentElement = element;
        String[] paths = xpath.split("/");
        for (int i = 0; i < paths.length; i++)
        {
            String pathPart = paths[i].trim();
            if (pathPart.isEmpty()) {
                continue;
            }
            
            Element newElement = element.getChild(pathPart);
            if (newElement == null) {
                newElement = new Element(pathPart);
            }
            currentElement.addContent(newElement);
            currentElement = newElement;
        }
        currentElement.setText(value);
    }

    @Override
    public String getMimeType()
    {
        return "application/xml";
    }

    @Override
    public String getFormat()
    {
        return "xml";
    }

    @Override
    public boolean supports(Locatable test)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return true;
    }
}
