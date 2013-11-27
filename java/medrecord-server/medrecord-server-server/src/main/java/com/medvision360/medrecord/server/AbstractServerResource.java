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
package com.medvision360.medrecord.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.medvision360.lib.server.ServerResourceBase;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.InvalidEHRIDException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.MedRecordService;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.spi.LocatableParser;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectID;
import org.restlet.representation.Representation;
import org.restlet.service.Service;

public abstract class AbstractServerResource
        extends ServerResourceBase
{
    protected <T extends Service> T getService(Class<T> serviceClass)
    {
        return getApplication().getServices().get(serviceClass);
    }
    
    protected MedRecordEngine engine() throws InitializationException
    {
        MedRecordService service = getService(MedRecordService.class);
        MedRecordEngine engine = service.engine();
        return engine;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getRequiredQueryValue(String name) throws MissingParameterException
    {
        String id = getQueryValue(name);
        if (id == null)
        {
            throw new MissingParameterException(name);
        }
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getRequiredAttributeValue(String name) throws MissingParameterException
    {
        String id = getAttribute(name);
        if (id == null)
        {
            throw new MissingParameterException(name);
        }
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected HierObjectID getHierObjectIDAttribute() throws MissingParameterException, InvalidEHRIDException
    {
        String id = getRequiredAttributeValue("id");
        try
        {
            return new HierObjectID(id);
        }
        catch (IllegalArgumentException e)
        {
            throw new InvalidEHRIDException(e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    protected Locatable toLocatable(Representation representation)
            throws NotSupportedException, InitializationException, IOException, ParseException
    {
        LocatableParser parser = engine().getLocatableParser("application/json", "json-pv");
        InputStream is = representation.getStream();
        return parser.parse(is);
    }

    protected IDList toIdList(Iterable<? extends ObjectID> list)
    {
        IDList result = new IDList();
        List<String> ids = Lists.newArrayList(Iterables.transform(list, new Function<ObjectID, String>()
        {
            @Override
            public String apply(ObjectID id)
            {
                return id.getValue();
            }
        }));
        result.setIds(ids);
        return result;
    }
}
