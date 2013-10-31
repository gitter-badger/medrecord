package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.rm.ehr.EHR;

public interface EHRSerializer // todo javadoc / api spec / exceptions
{
    public void serialize(EHR EHR, OutputStream os) throws IOException, SerializeException;

    public void serialize(EHR EHR, OutputStream os, String encoding) throws IOException, SerializeException;

    public String getMimeType();

    public String getFormat();
}
