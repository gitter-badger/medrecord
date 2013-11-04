/**
 * This file is part of MEDvision360 Profile Server..
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.server.resources;

import java.lang.reflect.InvocationTargetException;


import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.medrecord.api.demo.MrDemoResource;
import com.medvision360.medrecord.api.demo.MrDemoResult;
import org.restlet.resource.ResourceException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.medvision360.lib.server.ServerResourceBase;


/**
 *
 */
public class DemoServerResource
    extends ServerResourceBase
    implements MrDemoResource
{
    private final static Logger LOG =
        LoggerFactory.getLogger(DemoServerResource.class);

    @Override
    public MrDemoResult getResult() throws MissingParameterException
    {
        final String queryParam = getQueryValue("demo");
        if (queryParam == null)
        {
            throw new MissingParameterException("demo");
        }

        final MrDemoResult result = new MrDemoResult();
        result.setStuff(queryParam);
        return result;
    }
}
