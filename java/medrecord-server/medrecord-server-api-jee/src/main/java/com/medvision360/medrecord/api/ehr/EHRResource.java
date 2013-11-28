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

import com.medvision360.medrecord.api.EHR;
import com.medvision360.medrecord.api.exceptions.DeletedException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidEHRIDException;
import com.medvision360.medrecord.api.exceptions.InvalidLocatableIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

/**
 * @apipath /ehr/{id}
 * @apipathparam id An OpenEHR HierObjectID value identifying an EHR.
 *   [type=string,required,single,default=71350448-25BA-4395-B354-19B9CA9D5096]
 */
@SuppressWarnings("DuplicateThrows")
public interface EHRResource
{
    /**
     * Retrieve EHR resource.
     *
     * Retrieve basic info about an EHR as a JSON structure.
     * 
     * @apiqueryparam ignoreDeleted Set to true to return the info even if the EHR has been marked as deleted.
     *   [type=string,single,default=false]
     */
    @Get("json")
    public EHR getEHR()
            throws NotFoundException, DeletedException, ParseException, InvalidEHRIDException,
            RecordException, IORecordException;

    /**
     * Soft-delete EHR resource.
     * 
     * Delete a stored EHR. Fully deleting the entire EHR record is not possible. Rather, when deleting an EHR, 
     * it is simply marked as deleted. This also does not delete the contents of an EHR. In OpenEHR, 
     * actually deleting information in bulk is quite uncommon and it is not currently supported through this API.
     * 
     * The main reason to do actual erasure is in response to some kind of data protection request, 
     * and such purge actions should not be taken lightly. To erase all information for a particular subject from the 
     * server:
     * <ul>
     *     <li>fetch their EHR record and mark it as deleted</li>
     *     <li>iterate over all the locatables in their record and delete those</li>
     *     <li>delete the EHRStatus associated with the EHR record</li>
     *     <li>delete the Directory associated with the EHR record, if any</li>
     *     <li>query for all demographic locatables of or about the subject and delete those</li>
     * </ul>
     * 
     * This will leave behind only an anonymous, flagged-as-deleted EHR record, which can be used by the system to 
     * determine "there was information here but it has been erased".
     * 
     * Built-in API support for such a purge operation may be provided in the future. 
     */
    @Delete
    public void deleteEHR()
            throws NotFoundException, InvalidEHRIDException,
            RecordException, IORecordException;
}
