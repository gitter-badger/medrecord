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
package com.medvision360.medrecord.api.archetype;

import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InUseException;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

/**
 * @apipath /archetype/{id}
 * @apipathparam id An OpenEHR ArchetypeID value.
 *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
 */
@SuppressWarnings("DuplicateThrows")
public interface ArchetypeResource
{
    /**
     * Retrieve archetype resource.
     *
     * Retrieve an archetype encapsulated in JSON.
     */
    // note the use of | breaks the swagger UI generation magic doclet
    //@Get("json|xml")
    @Get("json")
    public ArchetypeResult getArchetype()
            throws NotFoundException, ParseException, InvalidArchetypeIDException,
            RecordException, IORecordException;

    /**
     * Retrieve archetype resource.
     *
     * Retrieve an archetype as an ADL string (plain text).
     *
     * @apiacceptvariant getArchetype
     */
    @Get("txt")
    public String getArchetypeAsText()
            throws NotFoundException, ParseException, InvalidArchetypeIDException,
            RecordException, IORecordException;

    /**
     * Delete archetype resource.
     * 
     * Delete a stored archetype.
     */
    @Delete
    public void deleteArchetype()
            throws NotFoundException, InUseException, InvalidArchetypeIDException,
            RecordException, IORecordException;
}
