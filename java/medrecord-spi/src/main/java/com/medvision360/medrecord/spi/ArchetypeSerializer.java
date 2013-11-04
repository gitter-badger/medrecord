package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.exceptions.SerializeException;

public interface ArchetypeSerializer extends TypeSelector // todo javadoc / api spec / exceptions
{
    public WrappedArchetype serialize(WrappedArchetype archetype, OutputStream os)
            throws IOException, SerializeException;

    public WrappedArchetype serialize(WrappedArchetype archetype, OutputStream os, String encoding)
            throws IOException, SerializeException;
}
