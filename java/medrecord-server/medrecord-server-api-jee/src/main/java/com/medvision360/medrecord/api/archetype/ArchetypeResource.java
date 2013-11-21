package com.medvision360.medrecord.api.archetype;

import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InUseException;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

/**
 * @apipath /archetype/{id}
 * @apipathparam id An OpenEHR ArchetypeID value.
 *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
 */
@SuppressWarnings("DuplicateThrows")
public interface ArchetypeResource
{
    /**
     * Retrieve archetype resource.
     *
     * Retrieve an archetype encapsulated in JSON.
     */
    // note the use of | breaks the swagger UI generation magic doclet
    //@Get("json|xml")
    @Get("json")
    public ArchetypeResult getArchetype()
            throws NotFoundException, MissingParameterException, ParseException, InvalidArchetypeIDException,
            RecordException, IORecordException;

    /**
     * Retrieve archetype resource.
     *
     * Retrieve an archetype as an ADL string (plain text).
     *
     * @apiacceptvariant getArchetype
     */
    @Get("txt")
    public String getArchetypeAsText()
            throws NotFoundException, MissingParameterException, ParseException, InvalidArchetypeIDException,
            RecordException, IORecordException;

    /**
     * Delete archetype resource.
     * 
     * Delete a stored archetype.
     */
    @Delete
    public void deleteArchetype()
            throws NotFoundException, MissingParameterException, InUseException, InvalidArchetypeIDException,
            RecordException, IORecordException;
}
