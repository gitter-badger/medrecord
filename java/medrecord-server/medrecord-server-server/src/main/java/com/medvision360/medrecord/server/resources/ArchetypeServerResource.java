package com.medvision360.medrecord.server.resources;

import java.io.IOException;

import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.medrecord.api.archetype.ArchetypeResource;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.openehr.rm.support.identification.ArchetypeID;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class ArchetypeServerResource
        extends AbstractServerResource
        implements ArchetypeResource
{
    @Override
    public Representation getArchetype() throws AnnotatedResourceException, RecordException, IOException
    {
        String id = getQueryValue("id");
        if (id == null)
        {
            throw new MissingParameterException("id");
        }
        
        ArchetypeID archetypeID = new ArchetypeID(id); // todo IAE should be some kind of annotated exception?

        MedRecordEngine engine = engine();
        WrappedArchetype wrappedArchetype = engine.getArchetypeStore().get(archetypeID);
        String asString = wrappedArchetype.getAsString();
        Representation representation = new StringRepresentation(asString);
        return representation;
    }
}
