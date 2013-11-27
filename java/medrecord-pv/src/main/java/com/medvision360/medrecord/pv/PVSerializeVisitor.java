/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
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
