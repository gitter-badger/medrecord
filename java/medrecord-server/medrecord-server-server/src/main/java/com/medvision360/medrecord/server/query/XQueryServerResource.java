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
package com.medvision360.medrecord.server.query;

import java.util.regex.Pattern;

import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.query.XQueryResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.XQueryStore;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class XQueryServerResource
        extends AbstractServerResource
        implements XQueryResource
{
    public final static Pattern defaultNamespacePattern = Pattern.compile(
            "declare\\s+default\\s+element\\s+namespace\\s+",
            Pattern.CASE_INSENSITIVE);
    public final static String defaultNamespaceDeclaration =
            "declare default element namespace \"http://schemas.openehr.org/v1\";\n";
    public final static Pattern openehrNamespacePattern = Pattern.compile(
            "declare\\s+namespace\\s+openehr[ =]",
            Pattern.CASE_INSENSITIVE);
    public final static String openehrNamespaceDeclaration =
            "declare namespace openehr = \"http://schemas.openehr.org/v1\";\n";
    public final static Pattern xsiNamespacePattern = Pattern.compile(
            "declare\\s+namespace\\s+xsi[ =]",
                    Pattern.CASE_INSENSITIVE);
    public final static String xsiNamespaceDeclaration =
            "declare namespace xsi = \"http://www.w3.org/2001/XMLSchema-instance\";\n";
    
    protected String addNamespaces(String q)
    {
        StringBuilder b = new StringBuilder();
        
        if (!defaultNamespacePattern.matcher(q).find())
        {
            b.append(defaultNamespaceDeclaration);
        }
        if (!openehrNamespacePattern.matcher(q).find())
        {
            b.append(openehrNamespaceDeclaration);
        }
        if (!xsiNamespacePattern.matcher(q).find())
        {
            b.append(xsiNamespaceDeclaration);
        }
        b.append(q);
        return b.toString();
    }
    
    @Override
    public Representation xQuery() throws RecordException
    {
        String q = getRequiredQueryValue("q");

        // Just to be quite clear about this: at this point q is a user-provided 'tainted' parameter.
        // we may be tempted at this point to attempt to somehow verify that this parameter is 'safe'
        // and/or somehow acceptable. However XQuery is not designed for that and BaseX definitely
        // is not designed for it either. Basically, making q 'safe' is not possible.
        //
        // The assumption is that this API is deployed safely behind some kind of AAA similar to what
        // you would use to secure a web based SSH console or SQL admin console.
        
        q = addNamespaces(q);
        
        try
        {
            engine().parseXQuery(q);
        }
        catch (ParseException e)
        {
            throw new ClientParseException(e.getMessage(), e);
        }
        String mimeType = engine().findMimeTypeForXQuery(q);
        if (mimeType == null)
        {
            mimeType = "text/plain"; // assumed that whatever we return is at least text
        }

        MediaType type = MediaType.valueOf(mimeType);
        XQueryStore store = engine().getLocatableStore();
        return new XQueryOutputRepresentation(type, store, q);
    }
}
