package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.api.exceptions.ParseException;

public interface ArchetypeParser extends TypeSelector // todo javadoc / api spec / exceptions
{
    public WrappedArchetype parse(InputStream is)
            throws IOException, ParseException;

    public WrappedArchetype parse(InputStream is, String encoding)
            throws IOException, ParseException;

}
