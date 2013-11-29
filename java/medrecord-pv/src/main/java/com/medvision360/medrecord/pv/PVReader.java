/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright (c) 2008-2010 Cambio Healthcare Systems, Sweden,
 *   Copyright (c) 2013 MEDvision360, The Netherlands.
 *   Licensed under the MPL 1.1/GPL 2.0/LGPL 2.1.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 * @author Rong Chen <rong.acode@gmail.com>
 * @author Erik Sundvall
 */
package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.rmutil.ExactPathComparator;
import com.medvision360.medrecord.rmutil.RMUtil;

public class PVReader extends RMUtil
{
    public SortedMap<String,String> toMap(InputStream is) throws IOException, ParseException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(is, JsonNode.class);

        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
        SortedMap<String, String> pv = new TreeMap<>(new ExactPathComparator());
        while (fields.hasNext())
        {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode node = field.getValue();
            JsonNodeType nodeType = node.getNodeType();
            String value;
            switch (nodeType)
            {
                case NULL:
                    value = null;
                    break;
                case BOOLEAN:
                case NUMBER:
                case STRING:
                    value = node.asText();
                    break;
                case ARRAY:
                case BINARY:
                case OBJECT:
                case MISSING:
                case POJO:
                default:
                    throw new ParseException(String.format(
                            "Path %s value is of type %s, need a primitive",
                            key, nodeType));
            }
            pv.put(key, value);
        }

        return pv;
    }
}
