package com.medvision360.medrecord.api.archetype;

import java.io.IOException;

import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.restlet.representation.Representation;
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
    @Get
    public Representation getArchetype() throws AnnotatedResourceException, IOException, RecordException;
}
