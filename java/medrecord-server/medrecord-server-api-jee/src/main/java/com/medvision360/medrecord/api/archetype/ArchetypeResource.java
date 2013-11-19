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
     * Retrieve an archetype as an ADL string (plain text).
     *
     * @apiqueryparam id An OpenEHR ArchetypeID value.
     *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     */
    @Get("txt")
    public String getArchetype()
            throws NotFoundException, MissingParameterException, RecordException, IOException;
    
    /**
     * Archetype resource.
     *
     * Retrieve an archetype encapsulated in JSON or XML.
     *
     * @apiqueryparam id An OpenEHR ArchetypeID value.
     *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     */
    // todo this breaks the swagger UI with
    //   Unable to read api 'com_medvision360_medrecord_api_archetype' from path
    //     http://localhost:8100/medrecord/v2/apidocs/com_medvision360_medrecord_api_archetype
    //     (server returned OK)
    //@Get("json|xml")
    //public ArchetypeResult getArchetypeResult()
    //        throws NotFoundException, MissingParameterException, RecordException, IOException;

}
