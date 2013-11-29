package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.medvision360.medrecord.rmutil.RMUtil;

public class PVWriter extends RMUtil
{
    protected JsonGenerator getJsonGenerator(OutputStream os, String encoding) throws IOException
    {
        JsonFactory jsonFactory = new JsonFactory();

        JsonEncoding jsonEncoding = null;
        try
        {
            jsonEncoding = JsonEncoding.valueOf(encoding);
        }
        catch (IllegalArgumentException e)
        {
            JsonEncoding[] options = JsonEncoding.values();
            for (int i = 0; i < options.length; i++)
            {
                JsonEncoding option = options[i];
                if (option.getJavaName().equals(encoding))
                {
                    jsonEncoding = option;
                    break;
                }
            }
        }
        if (jsonEncoding == null)
        {
            throw new IllegalArgumentException(String.format("%s is not a valid JSON encoding", encoding));
        }

        final JsonGenerator jg = jsonFactory.createGenerator(os, jsonEncoding);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());
        return jg;
    }

}
