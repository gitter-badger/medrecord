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
package com.medvision360.medrecord.api.query;

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidDateTimeException;
import com.medvision360.medrecord.api.exceptions.InvalidRangeException;
import com.medvision360.medrecord.api.exceptions.InvalidSubjectIDException;
import com.medvision360.medrecord.api.exceptions.InvalidSystemIDException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Get;

/**
 * @apipath /query/ehr
 */
@SuppressWarnings("DuplicateThrows")
public interface QueryEHRResource
{
    /**
     * Query EHR resources.
     * 
     * Retrieve a list of EHR IDs known to the server encapsulated in JSON, matching the constraints that are 
     * expressed as query parameters. All the query parameter types are AND-ed together. This means that a query like
     * <code>archetype=openEHR-EHR-OBSERVATION.blood_pressure.v1&archetypeQ=DEMOGRAPHIC</code> is guaranteed to never
     * return any results. However, when repeating the same parameter type multiple times, those are OR-ed together.
     * That means a query like <code>archetypeQ=body_weight&archetypeQ=blood_pressure</code> returns all locatables 
     * that are either body weights or blood pressures.
     * 
     * The query parameters are matched against the root locatable <em>or</em> any of its contents. 
     * So if you have a COMPOSITION containing a blood_pressure OBSERVATION, then a query
     * <code>archetypeQ=blood_pressure</code> will return that entire COMPOSITION (including, perhaps, 
     * other data besides the blood_pressure OBSERVATION). If this is not what you want, 
     * you need to use an advanced query such as <code>/query/xquery</code> or <code>/query/xquery/locatable</code>.
     * 
     * @apiqueryparam excludeDeleted Set to true to exclude EHRs that have been marked as deleted in the 
     *   returned list, to any other value to include them, or omit the parameter to have the implementation choose
     *   (typically using its most efficient option).
     * @apiqueryparam excludeEmpty Set to true to exclude EHRs that contain no locatables.
     *   [type=string,single,default=false]
     * @apiqueryparam systemID An OpenEHR HierObjectID value specifying a systemID to search. Specify multiple 
     *   times to search multiple systemIDs, or do not specify to search all systemIDs.
     *   [type=string,default=906C3435-8A06-4688-A9D0-CD233C1B072F]
     * @apiqueryparam subject An OpenEHR UIDBasedID value specifying a subject constraint on the EHRStatus 
     *   associated with the EHR.
     *   [type=string,single,default=0AC32288-684C-4D86-B7E6-6C21E18E4390]
     * @apiqueryparam createdBefore An ISO8601 DateTime value specifying a constraint on the maximum value of the 
     *   time the EHR was created.
     *   [type=string,single,default=20140101T00:00:00Z]
     * @apiqueryparam createdAfter An ISO8601 DateTime value specifying a constraint on the minimum value of the 
     *   time the EHR was created.
     *   [type=string,single,default=20130101T00:00:00Z]
     */
    @Get("json")
    public IDList ehrQuery()
            throws InvalidSystemIDException, InvalidSubjectIDException, InvalidDateTimeException,
            InvalidRangeException,
            RecordException, IORecordException;
}
