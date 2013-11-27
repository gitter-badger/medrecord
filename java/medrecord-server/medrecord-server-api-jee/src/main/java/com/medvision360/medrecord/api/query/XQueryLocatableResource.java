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

import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidQueryException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.UnsupportedQueryException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * @apipath /query/xquery/locatable
 */
@SuppressWarnings("DuplicateThrows")
public interface XQueryLocatableResource
{
    /**
     * XQuery locatable resources.
     * 
     * Executes the provided XQuery against all XQuery-capable storage mechanisms for locatables on the server 
     * (depending on the server configuration, some locatables may be stored in non-xquery-capable stores) and 
     * returns the result.
     * 
     * XQuery is a powerful query language, and accordingly this API provides a very advanced and powerful query 
     * mechanism, but it is important to realize that the trade-off in using it is that writing the queries and
     * interpreting the results may be harder, and that performance may be lower than for some of the simpler query 
     * options.
     * 
     * Roughly the same queries as supported by <code>/query/xquery</code> are supported, 
     * with one additional constraint: you must ensure that the results of your query are processable as a list
     * of locatable, that is, the result should have a structure like
     * 
     * <pre>
     *     &lt;composition&gt;...&lt;/composition&gt;
     *     &lt;composition&gt;...&lt;/composition&gt;
     *     &lt;composition&gt;...&lt;/composition&gt;
     *     ...
     * </pre>
     * 
     * or perhaps
     * 
     * <pre>
     *     &lt;content xsi:type="v1:OBSERVATION"&gt;...&lt;/content&gt;
     *     &lt;content xsi:type="v1:OBSERVATION"&gt;...&lt;/content&gt;
     *     &lt;content xsi:type="v1:OBSERVATION"&gt;...&lt;/content&gt;
     *     ...
     * </pre>
     * 
     * A good way to ensure this is the case is to do a specific query and then look for the nearest acceptable 
     * ancestor. For example,
     * 
     * <pre>
     *   declare default element namespace "http://schemas.openehr.org/v1";
     *   (: look for all compositions that contain blood_pressure measurements :)
     *   archetype_details/archetype_id/value/text()[contains(.,"blood_pressure")]/ancestor::*[self::composition]
     * </pre>
     * 
     * If you do not specify a default element namespace in your query, it is set to "http://schemas.openehr.org/v1".
     * The xsi and openehr namespaces are also declared for you unless you have declared them yourself.
     * 
     * @apiqueryparam q An XQuery, written to run against (a) collection(s) that contains openEHR XML data and to 
     *   return a list of xml fragments that can be parsed as locatables
     *   [type=string,required,single]
     * @apiqueryparam ehr An OpenEHR HierObjectID value specifying an EHR to search. Specify multiple times to search
     *   multiple EHRs, or do not specify to search all EHRs. Note that the nature of combining XQuery and non-XQuery
     *   constraints may mean that the OR-ing of this constraint with the XQuery constraint may not be very efficient
     *   or performant, depending on the server storage implementation(s) in use.
     *   [type=string,default=DC3BE110-DCF8-40C4-A8E3-AA1ADF78A959]
     */
    @Get("json")
    public Representation locatableXQuery()
            throws InvalidQueryException, UnsupportedQueryException,
            RecordException, IORecordException;
}
