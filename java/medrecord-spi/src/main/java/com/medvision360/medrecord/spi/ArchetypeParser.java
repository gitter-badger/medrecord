package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.spi.exceptions.ParseException;
import org.openehr.am.archetype.Archetype;

public interface ArchetypeParser // todo javadoc / api spec / exceptions
{
    public Archetype parse(InputStream is) throws IOException, ParseException;

    public Archetype parse(InputStream is, String encoding) throws IOException, ParseException;

    public String getMimeType();

    public String getFormat();
}
