package com.medvision360.medrecord.api.locatable;

import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidLocatableIDException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Patch;
import org.restlet.resource.Put;

/**
 * @apipath /locatable/{id}
 * @apipathparam id An OpenEHR UIDBasedID value identifying a Locatable.
 *   [type=string,required,single,default=71350448-25BA-4395-B354-19B9CA9D5096]
 */
@SuppressWarnings("DuplicateThrows")
public interface LocatableResource
{
    /**
     * Retrieve locatable resource.
     *
     * Retrieve a locatable as a path/value JSON structure.
     */
    @Get("json")
    public Representation getLocatable()
            throws NotFoundException, ParseException, InvalidLocatableIDException,
            RecordException, IORecordException;

    /**
     * Delete locatable resource.
     * 
     * Delete a stored locatable. While deleting locatables is supported by this API, it should be an extremely rare
     * event to actually do such a deletion outside of testing. In OpenEHR, locatables are typically not deleted but 
     * instead updated with new information, perhaps setting them to a different lifecycle state such as "suspended" 
     * or "expired".
     */
    @Delete
    public void deleteLocatable()
            throws NotFoundException, InvalidLocatableIDException,
            RecordException, IORecordException;

    /**
     * Update locatable resource.
     * 
     * Replace the contents of an existing locatable from a path/value JSON document. PUT cannot be used to create 
     * new locatables; you need to use POST for that, instead. Note that any paths that were present in a previous 
     * version of the locatable but are not present in the new JSON document result in removal of that information! 
     * Accordingly, like with all REST APIs, it is important to be careful with concurrent updates of locatables from 
     * multiple concurrent parties.
     * 
     * If there is a practical risk of multiple concurrent updates for a locatable, and you are only adding data, 
     * you may wish to use the PATCH version of this API call which keeps the non-mentioned path/value pairs intact 
     * and then only adds new information.
     * 
     * Support for safer concurrent requests using conditional puts and the locatable versioning model is planned but
     * not currently available.
     */
    @Put("json")
    public void putLocatable(Representation representation)
            throws NotFoundException, ClientParseException, NotSupportedException, ValidationException,
            RecordException, IORecordException;

    /**
     * Append to locatable resource.
     * 
     * Add new path/value contents to an existing locatable from a JSON document. This API call serializes a 
     * locatable into path/value pairs, then merges the newly supplied paths into that document, 
     * parses the merged JSON back into a locatable, and stores that locatable again on the server. Because this 
     * operation occurs on the server side and does not remove unknown paths, the risk of inadvertent removal of 
     * information in the case of multiple concurrent writes is much <em>lower</em> but it is <em>still there</em>.
     * 
     * Support for safer concurrent requests using conditional puts and the locatable versioning model is planned but
     * not currently available.
     */
    @Patch("json")
    public void patchLocatable(Representation representation)
            throws NotFoundException, ClientParseException, NotSupportedException, ValidationException,
            RecordException, IORecordException;
}
