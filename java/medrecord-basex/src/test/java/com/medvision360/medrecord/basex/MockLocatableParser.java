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
package com.medvision360.medrecord.basex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.medvision360.medrecord.spi.LocatableParser;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.HierObjectID;

class MockLocatableParser implements LocatableParser
{
    @Override
    public Locatable parse(InputStream is) throws IOException
    {
        return parse(is, "UTF-8");
    }

    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException
    {
        SAXBuilder builder = new SAXBuilder();
        Document d;
        try
        {
            d = builder.build(new InputStreamReader(is, encoding));
        }
        catch (JDOMException e)
        {
            throw new IOException(e);
        }

        HierObjectID uid = new HierObjectID(xpath(d, "//uid/value", Filters.element()));
        String archetypeNodeId = xpath(d, "//@archetype_node_id", Filters.attribute());
        DvText name = new DvText(xpath(d, "//name/value", Filters.element()));
        Archetyped archetypeDetails = new Archetyped(xpath(d, "//archetype_id/value", Filters.element()), "1.4");

        Locatable locatable = new MockLocatable(uid, archetypeNodeId, name, archetypeDetails, null, null, null);

        return locatable;
    }

    @Override
    public String getMimeType()
    {
        return "application/xml";
    }

    @Override
    public String getFormat()
    {
        return "xml";
    }

    @Override
    public boolean supports(Locatable test)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return true;
    }

    private <T> String xpath(Document d, String p, Filter<T> filter)
    {
        XPathExpression<T> xpath = XPathFactory.instance().compile(p, filter);
        T value = xpath.evaluateFirst(d);
        if (value == null)
        {
            return null;
        }
        if (value instanceof Element)
        {
            return ((Element) value).getValue();
        }
        if (value instanceof Attribute)
        {
            return ((Attribute) value).getValue();
        }
        return value.toString();
    }
}
