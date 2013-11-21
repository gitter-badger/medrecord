package com.medvision360.medrecord.basex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.medvision360.medrecord.riio.RIAdlConverter;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

public class XmlWrappedArchetypeConverter extends AbstractXmlConverter implements ArchetypeParser, ArchetypeSerializer
{
    private RIAdlConverter m_delegate;

    public XmlWrappedArchetypeConverter(RIAdlConverter delegate)
    {
        m_delegate = delegate;
    }

    public XmlWrappedArchetypeConverter()
    {
        this(new RIAdlConverter());
    }

    @Override
    public WrappedArchetype parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, "UTF-8");
    }

    @Override
    public WrappedArchetype parse(InputStream is, String encoding) throws IOException, ParseException
    {
        Document d = toDocument(is, encoding);
        
        String asString = xpath(d, "//asString", Filters.element());
//        System.out.println("--------------------------");
//        String[] lines = asString.split("\r?\n");
//        for (int i = 0; i < lines.length; i++)
//        {
//            String line = lines[i];
//            int lineNo = i + 1;
//            System.out.println(""+lineNo+": "+line);
//        }
//        System.out.println("--------------------------");
        boolean locked = Boolean.parseBoolean(xpath(d, "//locked", Filters.element()));

        InputStream asInputStream = IOUtils.toInputStream(asString);
        WrappedArchetype result = m_delegate.parse(asInputStream, encoding);
        return new WrappedArchetype(asString, result.getArchetype(), locked);
    }

    @Override
    public WrappedArchetype serialize(WrappedArchetype archetype, OutputStream os) throws IOException, SerializeException
    {
        return serialize(archetype, os, "UTF-8");
    }

    @Override
    public WrappedArchetype serialize(WrappedArchetype archetype, OutputStream os, String encoding)
            throws IOException, SerializeException
    {
        String asString = archetype.getAsString();
        if (asString == null || "".equals(asString))
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            m_delegate.serialize(archetype, bos, encoding);
            byte[] bytes = bos.toByteArray();
            asString = new String(bytes, encoding);
        }
        
        Element root = new Element("archetype");
        set(root, "/asString", asString);
        set(root, "/locked", Boolean.toString(archetype.isLocked()));

        Document d = new Document(root);

        outputDocument(d, os, encoding);
        return new WrappedArchetype(asString, archetype.getArchetype(), archetype.isLocked());
    }

    @Override
    public String getFormat()
    {
        return "wrapped-archetype-xml";
    }

}
