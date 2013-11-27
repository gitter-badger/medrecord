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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.medvision360.medrecord.memstore.MemArchetypeStore;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Locatable;
import com.medvision360.medrecord.spi.tck.TestMeasurementService;
import com.medvision360.medrecord.spi.tck.TestTerminologyService;

public class PVConverterTest extends LocatableConverterTCKTestBase
{
    protected ArchetypeStore m_archetypeStore;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        m_archetypeStore = new MemArchetypeStore();
        m_archetypeStore.initialize();
        Archetype archetype = loadArchetype();
        m_archetypeStore.insert(archetype);
    }

    @Override
    protected LocatableParser getParser() throws Exception
    {
        PVParser parser = new PVParser(new TestTerminologyService(), new TestMeasurementService(), encoding, lang, 
            territory());
        parser.setRmVersion("1.0.2");
        return parser;
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new PVSerializer();
    }
    
    public void testFillingInOptionalValues() throws Exception
    {
        String[] sample = {
          "[%s]/archetype_node_id" , "at0001",
          "[%s]/content/collection_type" , "LIST",
          "[%s]/content[%s][1]/data[at0003]/items/collection_type" , "LIST",
          "[%s]/content[%s][1]/data[at0003]/items[at0004][1]/rm_entity" , "Element",
          "[%s]/content[%s][1]/data[at0003]/items[at0004][1]/name/value" , "header",
          "[%s]/content[%s][1]/data[at0003]/items[at0004][1]/value/value" , "date",
          "[%s]/content[%s][1]/data[at0003]/items[at0005][2]/rm_entity" , "Element",
          "[%s]/content[%s][1]/data[at0003]/items[at0005][2]/name/value" , "value",
          "[%s]/content[%s][1]/data[at0003]/items[at0005][2]/value/value" , "2008-05-17",
        };
        for (int i = 0; i < sample.length; i++)
        {
            String path = sample[i];
            sample[i] = String.format(path, "unittest-EHR-COMPOSITION.composition.v1",
                    "unittest-EHR-ADMIN_ENTRY.date.v2");
        }

        String json = toJSON(sample);
        System.out.println(json);
        ByteArrayInputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"));
        Locatable locatable = m_parser.parse(is);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(locatable, os);
        String result = os.toString("UTF-8");
        System.out.println(result);
        assertTrue(result.contains("/category"));
        assertTrue(result.contains("/language"));
        assertTrue(result.contains("/territory"));
        assertTrue(result.contains("/name"));
        assertTrue(result.contains("/composer"));
        assertTrue(result.contains("/subject"));
        assertTrue(result.contains("DvDate"));
        assertTrue(result.contains("DvText"));
    }

    private String toJSON(String[] pv)
    {
        StringBuilder b = new StringBuilder();
        b.append("{\n");
        for (int i = 0; i < pv.length; i++)
        {
            String path = pv[i++];
            String value = pv[i];
            b.append("  \"");
            b.append(path);
            b.append("\" : \"");
            b.append(value);
            b.append("\",\n");
        }
        b.deleteCharAt(b.length()-2);
        b.append("}");
        return b.toString();
    }
}
