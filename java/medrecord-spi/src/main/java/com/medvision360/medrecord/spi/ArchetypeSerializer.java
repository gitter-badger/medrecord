package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.am.archetype.Archetype;

public interface ArchetypeSerializer // todo javadoc / api spec / exceptions
{
    public void serialize(Archetype archetype, OutputStream os) throws IOException, SerializeException;

    public void serialize(Archetype archetype, OutputStream os, String encoding) throws IOException, SerializeException;

    public String getMimeType();

    public String getFormat();
}
