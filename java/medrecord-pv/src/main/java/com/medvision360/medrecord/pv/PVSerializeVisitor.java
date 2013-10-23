package com.medvision360.medrecord.pv;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public class PVSerializeVisitor implements SerializeVisitor
{
    private final JsonGenerator m_jg;

    public PVSerializeVisitor(JsonGenerator jg)
    {
        m_jg = jg;
    }

    @Override
    public void pair(String path, Object value) throws IOException
    {
        if (value == null)
        {
            m_jg.writeNullField(path);
        }
        else if (value instanceof Boolean)
        {
            m_jg.writeBooleanField(path, (Boolean) value);
        }
        else if (value instanceof Float)
        {
            m_jg.writeNumberField(path, (Float) value);
        }
        else if (value instanceof Double)
        {
            m_jg.writeNumberField(path, (Double) value);
        }
        else if (value instanceof Integer)
        {
            m_jg.writeNumberField(path, (Integer) value);
        }
        else if (value instanceof Long)
        {
            m_jg.writeNumberField(path, (Long) value);
        }
        else if (value instanceof Short)
        {
            m_jg.writeNumberField(path, (Short) value);
        }
        else
        {
            m_jg.writeStringField(path, value.toString());
        }
    }
}
