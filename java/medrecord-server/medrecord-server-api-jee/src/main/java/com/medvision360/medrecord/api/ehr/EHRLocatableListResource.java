package com.medvision360.medrecord.api.ehr;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @apipath /ehr/{id}/locatable
 * @apipathparam id An OpenEHR HierObjectID value identifying an EHR.
 *   [type=string,required,single,default=71350448-25BA-4395-B354-19B9CA9D5096]
 */
@SuppressWarnings("DuplicateThrows")
public interface EHRLocatableListResource
{
    /**
     * Create locatable resource in this EHR.
     * 
     * Store a new locatable into the specified EHR from a path/value JSON document. The provided locatable does not 
     * need unique identifiers; if those are not provided they will be added by the server. If you do provide an 
     * identifier, it will result in DUPLICATE_EXCEPTION if the locatable already exists (use PUT to update an 
     * existing locatable).
     */
    @Post("json")
    public ID postLocatable(Representation representation)
            throws DuplicateException, ClientParseException, NotSupportedException, ValidationException,
            RecordException, IORecordException;

    /**
     * List locatable resources in this EHR.
     * 
     * Retrieve a list of all locatable IDs known to the server that are part of the specified EHR, encapsulated in 
     * JSON. This API call is of limited use in most practical scenarios, because the result can become very large, and
     * because typically you will want to constrain the results by some kind of selection or query, like looking only
     * for locatables of a particular type. Use the <code>/query</code> API for that.
     * 
     * However, this API call is provided nonetheless, for API consistency, completeness and ease of testing.
     * 
     * @apiqueryparam ignoreDeleted Set to true to return the info even if the EHR has been marked as deleted.
     *   [type=string,single,default=false]
     */
    @Get
    public IDList listLocatables()
            throws ParseException, RecordException, IORecordException;
}
