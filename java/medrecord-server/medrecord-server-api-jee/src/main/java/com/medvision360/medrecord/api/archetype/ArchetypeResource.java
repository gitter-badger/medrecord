package com.medvision360.medrecord.api.archetype;

import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.lib.api.ServiceUnavailableException;
import org.restlet.resource.Get;

/**
 * @apipath /archetype
 */
public interface ArchetypeResource
{
    /**
     * Demo resource.
     *
     * Some extra text which shows up in swagger...
     *
     * @apiqueryparam id An OpenEHR ArchetypeID value.
     *   [type=string,required,single,default=openEHR-EHR-OBSERVATION.blood_pressure.v1]
     */
    @Get("txt")
    public String getArchetype() throws
            ArchetypeNotFoundException, MissingParameterException, ServiceUnavailableException;

    // @Get("txt") means it returns plain/text
    // You almost never have to return an explicit Representation. Restlet is smart enough to convert it.
    // (TODO: the api documentation tool does not support plain text responses yet...)
    // Only use exceptions derived from AnnotatedResourceException here, by listing them all explicitly here they
    // will show up in the API documentation.


}
