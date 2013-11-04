/**
 * This file is part of MEDvision360 Profile Server..
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.api.demo;

import com.medvision360.lib.api.MissingParameterException;
import org.restlet.resource.Get;

/**
 * @apipath /demo
 */
public interface MrDemoResource
{
    /**
     * Demo resource.
     *
     * Some extra text which shows up in swagger...
     *
     * @apiqueryparam demo A demo query parameter.
     *   [type=string,required,single,default=hello]
     */
    @Get("json|xml")
    public MrDemoResult getResult() throws
            MissingParameterException;
}
