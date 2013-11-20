package com.medvision360.medrecord.riio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.apache.commons.io.IOUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.am.serialize.ADLSerializer;
import se.acode.openehr.parser.ADLParser;
import se.acode.openehr.parser.TokenMgrError;

public class RIAdlConverter implements ArchetypeParser, ArchetypeSerializer
{
    boolean m_missingLanguageCompatible = true;
    boolean m_emptyPurposeCompatible = true;

    public RIAdlConverter()
    {
    }

    public RIAdlConverter(boolean missingLanguageCompatible, boolean emptyPurposeCompatible)
    {
        m_missingLanguageCompatible = missingLanguageCompatible;
        m_emptyPurposeCompatible = emptyPurposeCompatible;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setMissingLanguageCompatible(boolean missingLanguageCompatible)
    {
        m_missingLanguageCompatible = missingLanguageCompatible;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEmptyPurposeCompatible(boolean emptyPurposeCompatible)
    {
        m_emptyPurposeCompatible = emptyPurposeCompatible;
    }

    @Override
    public WrappedArchetype parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, "UTF-8");
    }

    @Override
    public WrappedArchetype parse(InputStream is, String encoding) throws IOException, ParseException
    {
        String asString = IOUtils.toString(is, encoding);
        ADLParser adlParser = new ADLParser(asString, m_missingLanguageCompatible, m_emptyPurposeCompatible);
        Archetype archetype;
        try
        {
            archetype = adlParser.parse();
        }
        catch (Exception|TokenMgrError e)
        {
            throw new ParseException(e);
        }
        asString = stripBOMAndStartingWhiteSpace(asString);
        
        return new WrappedArchetype(asString, archetype);
    }

    private String stripBOMAndStartingWhiteSpace(String asString)
    {
        // ADL files often have a unicode BOM at the start of the file (which is bad/not recommended for UTF-8)
        //   we definitely want that gone as we are (de)encapsulating the ADL into XML and/or sending it over the
        //   wire.
        return asString.replaceFirst("^\uFEFF\\s*", "");
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
            ADLSerializer adlSerializer = new ADLSerializer();
            asString = adlSerializer.output(archetype.getArchetype());
        }
        asString = stripBOMAndStartingWhiteSpace(asString);
        IOUtils.write(asString, os, encoding);
        return new WrappedArchetype(asString, archetype.getArchetype());
    }

    @Override
    public String getMimeType()
    {
        return "text/plain";
    }

    @Override
    public String getFormat()
    {
        return "adl";
    }
}
