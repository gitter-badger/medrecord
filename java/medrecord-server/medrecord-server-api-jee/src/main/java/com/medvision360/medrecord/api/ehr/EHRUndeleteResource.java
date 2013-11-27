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
package com.medvision360.medrecord.api.ehr;

import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Post;

/**
 * @apipath /ehr/{id}/undelete
 * @apipathparam id An OpenEHR HierObjectID value identifying an EHR.
 *   [type=string,required,single,default=71350448-25BA-4395-B354-19B9CA9D5096]
 */
@SuppressWarnings("DuplicateThrows")
public interface EHRUndeleteResource
{
    /**
     * Undelete EHR resource.
     * 
     * Restores an existing EHR that has been deleted. Basically this removes the 'deleted' flag from the EHR record 
     * that was put there by a previous <code>DELETE /ehr/{id}</code>.
     */
    @Post
    public void undeleteEHR()
            throws NotFoundException, ClientParseException,
            RecordException, IORecordException;
}
