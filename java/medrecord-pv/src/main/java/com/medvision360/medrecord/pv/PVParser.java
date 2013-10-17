package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableParser;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

public class PVParser implements LocatableParser
{
    private ArchetypeStore m_archetypeStore;

    public PVParser(ArchetypeStore archetypeStore)
    {
        m_archetypeStore = archetypeStore;
    }

    @Override
    public Locatable parse(InputStream is) throws IOException
    {
        throw new UnsupportedOperationException("todo implement PVParser.parse()");
    }

    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException
    {
        throw new UnsupportedOperationException("todo implement PVParser.parse()");
    }

    @Override
    public String getMimeType()
    {
        return "application/json";
    }

    @Override
    public String getFormat()
    {
        return "json-pv";
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
