package com.medvision360.medrecord.server.resources;

import java.io.IOException;

import com.medvision360.medrecord.api.archetype.ArchetypeResource;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import com.medvision360.medrecord.spi.exceptions.MissingParameterException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.openehr.rm.support.identification.ArchetypeID;
import org.restlet.resource.Get;

public class ArchetypeServerResource
        extends AbstractServerResource
        implements ArchetypeResource
{
    @Override
    public ArchetypeResult getArchetype() throws RecordException, IOException
    {
        WrappedArchetype wrappedArchetype = getWrappedArchetype();
        String adl = wrappedArchetype.getAsString();
        String id = wrappedArchetype.getArchetype().getArchetypeId().getValue();
        ArchetypeResult result = new ArchetypeResult();
        result.setArchetypeId(id);
        result.setArchetype(adl);
        return result;
    }

    @Override
    public String getArchetypeAsText() throws RecordException, IOException
    {
        WrappedArchetype wrappedArchetype = getWrappedArchetype();
        return wrappedArchetype.getAsString();
    }

    private WrappedArchetype getWrappedArchetype() throws RecordException, IOException
    {
        String id = getRequiredQueryValue("id");
        ArchetypeID archetypeID = new ArchetypeID(id);

        MedRecordEngine engine = engine();
        return engine.getArchetypeStore().get(archetypeID);
    }
}
