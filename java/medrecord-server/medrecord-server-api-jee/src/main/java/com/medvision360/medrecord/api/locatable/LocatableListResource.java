package com.medvision360.medrecord.api.locatable;

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
 * @apipath /locatable
 */
@SuppressWarnings("DuplicateThrows")
public interface LocatableListResource
{
    /**
     * Create locatable resource outside EHR.
     * 
     * Store a new locatable from a path/value JSON document. The provided locatable does not need unique identifiers;
     * if those are not provided they will be added by the server. If you do provide an identifier, it will result in 
     * DUPLICATE_EXCEPTION if the locatable already exists (use PUT to update an existing locatable).
     * 
     * This API call will not associated the locatable with any EHR record. This may be appropriate, 
     * especially for demographics information, but for most locatables, you should use
     * <code>/ehr/{id}/locatable</code> to store a locatable inside of an EHR record. Note it is not possible to 
     * associate a locatable with an EHR after it has been created.
     */
    @Post("json")
    public ID postLocatable(Representation representation)
            throws DuplicateException, ClientParseException, NotSupportedException, ValidationException,
            RecordException, IORecordException;

    /**
     * List locatable resources.
     * 
     * Retrieve a list of all locatable IDs known to the server encapsulated in JSON. This API call is of very limited 
     * use in most practical scenarios, because the result can become very large, and because typically you will want 
     * to constrain the results by some kind of selection or query, like looking only for locatables associated with a 
     * particular patient or caregiver. Use the <code>/ehr/{id}/locatable</code> API to constrain the list to a 
     * particular EHR, or use the <code>/query</code> API for more complex queries.
     * 
     * However, this API call is provided nonetheless, for API consistency, completeness and ease of testing.
     */
    @Get
    public IDList listLocatables()
            throws ParseException, RecordException, IORecordException;
}
