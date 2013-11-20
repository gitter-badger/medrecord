package com.medvision360.medrecord.api.archetype;

import java.io.IOException;

import com.medvision360.medrecord.spi.exceptions.MissingParameterException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.restlet.resource.Get;

/**
 * @apipath /archetype
 */
@SuppressWarnings("DuplicateThrows")
public interface ArchetypeResource
{
    /**
     * Archetype resource.
     *
     * Retrieve an archetype encapsulated in JSON or XML.
     *
     * @apiqueryparam id An OpenEHR ArchetypeID value.
     *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     */
    // note the use of | breaks the swagger UI generation magic doclet
    //@Get("json|xml")
    @Get("json")
    public ArchetypeResult getArchetype()
            throws NotFoundException, MissingParameterException, RecordException, IOException;

    /**
     * Archetype resource.
     *
     * Retrieve an archetype as an ADL string (plain text).
     *
     * @apiacceptvariant getArchetype
     */
    @Get("txt")
    public String getArchetypeAsText()
            throws NotFoundException, MissingParameterException, RecordException, IOException;
}
