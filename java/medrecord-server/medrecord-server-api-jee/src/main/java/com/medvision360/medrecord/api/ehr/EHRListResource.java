package com.medvision360.medrecord.api.ehr;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @apipath /ehr
 */
@SuppressWarnings("DuplicateThrows")
public interface EHRListResource
{
    /**
     * Create EHR resource.
     * 
     * Creates a new EHR along with an EHRStatus from a path/value JSON document. The provided EHR and EHRStatus do 
     * not need unique identifiers; if those are not provided they will be added by the server. If you do provide an 
     * identifier, it will result in DUPLICATE_EXCEPTION if the EHR and/or EHRStatus already exists (use PUT on the 
     * <code>/locatable</code> API to update an existing EHRStatus).
     * 
     * Returns the ID of the new EHR wrapped in a JSON document.
     */
    @Post("json")
    public ID postEHR(Representation representation)
            throws DuplicateException, ClientParseException,
            RecordException, IORecordException;

    /**
     * List EHR resources.
     * 
     * Retrieve a list of HierObjectIDs (typically UUIDs, but not always) for all EHRs known to the server 
     * encapsulated in JSON. This API call is of limited use in most practical scenarios, because the result can become
     * very large, and because typically you will want to constrain the results by some kind of selection or query, 
     * like looking only for particular subjects and their EHRs. Use the <code>/query</code> API for that.
     * 
     * However, this API call is provided nonetheless, for API consistency, completeness and ease of testing.
     * 
     * @apiqueryparam excludeDeleted Set to true to exclude EHRs that have been marked as deleted in the 
     *   returned list, to any other value to include them, or omit the parameter to have the implementation choose
     *   (typically using its most efficient option).
     *   [type=string,single,default=false]
     */
    @Get
    public IDList listEHRs()
            throws PatternException, ParseException, RecordException, IORecordException;
}
