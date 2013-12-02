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

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.query.QueryLocatableResource;
import com.medvision360.medrecord.server.AbstractServerResource;

public class QueryLocatableServerResource
        extends AbstractServerResource
        implements QueryLocatableResource
{
    @Override
    public IDList locatableQuery() throws RecordException
    {
        String q = getRequiredQueryValue("q");

        // Just to be quite clear about this: at this point q is a user-provided 'tainted' parameter.
        // we may be tempted at this point to attempt to somehow verify that this parameter is 'safe'
        // and/or somehow acceptable. However XQuery is not designed for that and BaseX definitely
        // is not designed for it either. Basically, making q 'safe' is not possible.
        //
        // The assumption is that this API is deployed safely behind some kind of AAA similar to what
        // you would use to secure a web based SSH console or SQL admin console.

        throw new AnnotatedUnsupportedOperationException("todo implement");
    }
}
