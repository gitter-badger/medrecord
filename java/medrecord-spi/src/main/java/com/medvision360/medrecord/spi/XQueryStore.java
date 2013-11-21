/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;

public interface XQueryStore extends LocatableStore
{

    /**
     * Execute the provided query against the store, returning matched locatables. The provided query has to result in a
     * list of 0 or more locatable instances, i.e. it should be written to return whole documents as XML rather than
     * attempting to extract their contents. The returned result is immutable. The returned result is empty if there are
     * 0 results.
     * <p/>
     * An example of an acceptable query:
     * <pre>
     *     xquery version "3.0";
     *     for $x in collection()
     *       /PERSON[
     *         \@archetype_id='openEHR-DEMOGRAPHIC-PERSON.mobiguide_vmr_person_evaluated_person.v1'
     *       ]
     *     return $x
     * </pre>
     *
     * @param XQuery the query to execute.
     * @return an iterable of locatables. Can be empty if there are no matches.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided query cannot be parsed or understood.
     * @throws com.medvision360.medrecord.api.exceptions.NotSupportedException if the provided query seems valid but it
     * uses an XQuery feature the store does not support, or it cannot be supported by the store for another reason.
     * @throws java.io.IOException if another error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<Locatable> query(String XQuery) throws NotSupportedException, IOException;

    /**
     * Execute the provided query against the store, returning matched locatables in the specified EHR. The same 
     * rules otherwise apply as for {@link #query(String)}.
     *
     * @param EHR the EHR record to constrain the queries to.
     * @param XQuery the query to execute.
     * @return an iterable of locatables. Can be empty if there are no matches.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided query cannot be parsed or understood.
     * @throws com.medvision360.medrecord.api.exceptions.NotSupportedException if the provided query seems valid but it
     * uses an XQuery feature the store does not support, or it cannot be supported by the store for another reason.
     * @throws java.io.IOException if another error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<Locatable> query(EHR EHR, String XQuery) throws NotSupportedException, IOException;

    /**
     * Execute the provided query against the store, writing results to the provided output stream. Typically the
     * results will be written as UTF-8 XML, but, certain store implementations support different output formats through
     * custom declarations within the query. For example, <a href="http://basex.org/">BaseX</a> supports the construct
     * <p/>
     * <pre>
     *     xquery declare option output:method 'jsonml';
     * </pre>
     * <p/>
     * to provide JSONML output instead of XML output.
     *
     * @param XQuery the query to execute.
     * @param os the output stream to write the results to.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided query cannot be parsed or understood.
     * @throws NotSupportedException if the provided query seems valid but it uses an XQuery feature the store does not
     * support, or it cannot be supported by the store for another reason.
     * @throws IOException if another error occurs interacting with storage.
     */
    public void query(String XQuery, OutputStream os) throws NotSupportedException, IOException;

}
