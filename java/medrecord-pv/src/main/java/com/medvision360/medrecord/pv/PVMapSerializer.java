package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.SortedMap;

import com.fasterxml.jackson.core.JsonGenerator;

public class PVMapSerializer extends PVWriter
{
    public void serialize(SortedMap<String, Object> map, OutputStream os, String encoding) throws IOException
    {
        JsonGenerator jg = getJsonGenerator(os, encoding);
        PVSerializeVisitor visitor = new PVSerializeVisitor(jg);
        jg.writeStartObject();
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            visitor.pair(key, value);
        }
        jg.writeEndObject();
        jg.close();
    }
}
