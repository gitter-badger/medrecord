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
import java.io.OutputStream;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class AbstractXmlConverter
{
    @SuppressWarnings("UnusedDeclaration")
    public String getMimeType()
    {
        return "application/xml";
    }

    protected <T> String xpath(org.jdom2.Document d, String p, Filter<T> filter)
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

    protected void set(Element element, String xpath, String value)
    {
        Element currentElement = element;
        String[] paths = xpath.split("/");
        for (int i = 0; i < paths.length; i++)
        {
            String pathPart = paths[i].trim();
            if (pathPart.isEmpty())
            {
                continue;
            }

            Element newElement = element.getChild(pathPart);
            if (newElement == null)
            {
                newElement = new Element(pathPart);
                currentElement.addContent(newElement);
            }
            currentElement = newElement;
        }
        currentElement.setText(value);
    }

    protected Document toDocument(InputStream is, String encoding) throws IOException
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
        return d;
    }
    
    protected void outputDocument(Document d, OutputStream os, String encoding) throws IOException
    {
        Format format = Format.getPrettyFormat();
        format.setTextMode(Format.TextMode.PRESERVE); // this is the default, set just to be very safe
        format.setEncoding(encoding);
        XMLOutputter output = new XMLOutputter(format);
        output.output(d, os);
    }
}
