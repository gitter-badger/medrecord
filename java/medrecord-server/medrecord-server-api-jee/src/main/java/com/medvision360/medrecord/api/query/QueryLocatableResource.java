package com.medvision360.medrecord.api.query;

import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Get;

/**
 * @apipath /query/locatable
 */
@SuppressWarnings("DuplicateThrows")
public interface QueryLocatableResource
{
    /**
     * Query locatable resources.
     * 
     * Retrieve a list of locatable IDs known to the server encapsulated in JSON, matching the constraints that are 
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
     * @apiqueryparam ehr An OpenEHR HierObjectID value specifying an EHR to search. Specify multiple times to search
     *   multiple EHRs, or do not specify to search all EHRs.
     *   [type=string,default=DC3BE110-DCF8-40C4-A8E3-AA1ADF78A959]
     * @apiqueryparam rmOriginator A string specifying a reference model originator (such as "openEHR") to 
     *   constrain results to. The provided parameter is compared against the archetype ID of the locatable and any 
     *   nested locatable contents. Specify 
     *   multiple times to allow multiple rm originators, or do not specify to not constrain the results at all.
     *   [type=string,default=openEHR]
     * @apiqueryparam rmName A string specifying a reference model name (such as "EHR" or "DEMOGRAPHIC") to 
     *   constrain results to. The provided parameter is compared against the archetype ID of the locatable and any 
     *   nested locatable contents. Specify 
     *   multiple times to allow multiple rm names, or do not specify to not constrain the results at all.
     *   [type=string,default=EHR]
     * @apiqueryparam conceptName A string specifying a reference model concept (such as "lab_test", 
     *   "medication" or "person) to constrain results to. The provided parameter is compared against the archetype ID
     *   of the locatable and any nested locatable contents. Specify multiple times to allow multiple rm concepts, or
     *   do not specify to not constrain the results at all.
     *   [type=string,default=blood_pressure]
     * @apiqueryparam archetype A string specifying an archetype name to constrain results to. The provided parameter 
     *   is compared against the archetype ID of the locatable and any nested locatable contents. Specify multiple times
     *   to allow multiple archetypes, or do not specify to not constrain the results at all.
     *   [type=string,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     * @apiqueryparam archetypeQ A regular expression qualifying archetype names to constrain results to. 
     *   The provided parameter is compared against the archetype ID of the locatable.
     *   [type=string,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     */
    @Get("json")
    public IDList locatableQuery()
            throws InvalidArchetypeIDException, AnnotatedIllegalArgumentException, PatternException,
            RecordException, IORecordException;
}
