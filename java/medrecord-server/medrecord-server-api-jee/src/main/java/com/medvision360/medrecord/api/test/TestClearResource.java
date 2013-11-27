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
package com.medvision360.medrecord.api.test;

import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Post;

/**
 * @apipath /test/clear
 * @apiqueryparam confirm Set to the value "CONFIRM" to confirm you wish to do this.
 *   [type=string,required,single,default=false]
 */
public interface TestClearResource
{
    /**
     * Clear all databases.
     *
     * Empty out the server databases completely, removing all locatables,  all archetypes, all EHRs, 
     * and any other stored data. This is a <strong>very</strong> destructive method intended for use with 
     * (unit) testing only.
     */
    @Post
    public void clear() throws RecordException;
}
