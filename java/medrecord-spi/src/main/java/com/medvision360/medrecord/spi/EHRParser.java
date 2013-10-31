package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.spi.exceptions.ParseException;
import org.openehr.rm.ehr.EHR;

public interface EHRParser // todo javadoc / api spec / exceptions
{
    public EHR parse(InputStream is) throws IOException, ParseException;

    public EHR parse(InputStream is, String encoding) throws IOException, ParseException;

    public String getMimeType();

    public String getFormat();
}
