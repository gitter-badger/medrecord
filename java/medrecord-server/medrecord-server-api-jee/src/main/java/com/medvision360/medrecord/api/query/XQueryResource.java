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
 * @apipath /query/xquery
 */
@SuppressWarnings("DuplicateThrows")
public interface XQueryResource
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
     * The server implementation makes no attempt to do any processing on the results of your query. Depending on the
     * backing implementation and the queries you write, this may expose you to some implementation specifics in the 
     * query results. It also means that the result may or may not be valid XML, and accordingly, it is returned as a
     * string. If you wish to retrieve the results in processed form, like a nested map of path/value 
     * locatables, you may wish to use the <code>/query/xquery/locatable</code> API instead. That API also allows you
     * to limit to a specific EHR.
     * 
     * All kinds of XQuery are supported. You can use XPath expressions such as
     * <pre>
     *   declare default element namespace "http://schemas.openehr.org/v1";
     *   (: find all archetypes that are in use :)
     *   distinct-values(//archetype_details/archetype_id/value/text()[concat(., ' ')])
     * </pre>
     * 
     * Or you can use FLWOR queries such as
     * <pre>
     *   declare default element namespace "http://schemas.openehr.org/v1";
     *   (: dump all compositions (probably will cause OOM) :)
     *   for $x in collection()/composition
     *     return $x
     * </pre>
     * 
     * or perhaps
     * <pre>
     *   declare default element namespace "http://schemas.openehr.org/v1";
     *   declare namespace openehr = "http://schemas.openehr.org/v1";
     *   declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
     *   (: dump all observations (probably will cause OOM) :)
     *   for $x in collection()//*[contains(@xsi:type,"OBSERVATION")] return $x
     * </pre>
     * 
     * If you do not specify a default element namespace in your query, it is set to "http://schemas.openehr.org/v1".
     * The xsi and openehr namespaces are also declared for you unless you have declared them yourself. This means 
     * that the following query has the same result as the one above:
     * <pre>
     *   for $x in collection()//openehr:*[contains(@xsi:type,"OBSERVATION")] return $x
     * </pre>
     * 
     * @apiqueryparam q An XQuery, written to run against (a) collection(s) that contains openEHR XML data
     *   [type=string,required,single,default=//archetype_details/archetype_id/value/text()]
     */
    @Get("txt")
    public Representation xQuery()
            throws InvalidQueryException, UnsupportedQueryException,
            RecordException, IORecordException;
}
