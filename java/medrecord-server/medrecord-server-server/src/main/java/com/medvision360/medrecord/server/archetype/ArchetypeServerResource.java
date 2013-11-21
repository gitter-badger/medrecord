package com.medvision360.medrecord.server.archetype;

import java.io.IOException;

import com.medvision360.medrecord.api.archetype.ArchetypeResource;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidArchetypeIDException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.openehr.rm.support.identification.ArchetypeID;

public class ArchetypeServerResource
        extends AbstractServerResource
        implements ArchetypeResource
{
    @Override
    public ArchetypeResult getArchetype() throws RecordException
    {
        WrappedArchetype wrappedArchetype = getWrappedArchetype();
        String adl = wrappedArchetype.getAsString();
        String id = wrappedArchetype.getArchetype().getArchetypeId().getValue();
        ArchetypeResult result = new ArchetypeResult();
        result.setArchetypeId(id);
        result.setAdl(adl);
        return result;
    }

    @Override
    public String getArchetypeAsText() throws RecordException
    {
        WrappedArchetype wrappedArchetype = getWrappedArchetype();
        return wrappedArchetype.getAsString();
    }

    @Override
    public void deleteArchetype() throws RecordException
    {
        try
        {
            ArchetypeID archetypeID = getArchetypeIDAttribute();
            engine().getArchetypeStore().delete(archetypeID);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    private WrappedArchetype getWrappedArchetype() throws RecordException
    {
        try
        {
            ArchetypeID archetypeID = getArchetypeIDAttribute();
    
            MedRecordEngine engine = engine();
            return engine.getArchetypeStore().get(archetypeID);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    private ArchetypeID getArchetypeIDAttribute() throws MissingParameterException, InvalidArchetypeIDException
    {
        String id = getRequiredAttributeValue("id");
        try
        {
            return new ArchetypeID(id);
        }
        catch (IllegalArgumentException e)
        {
            throw new InvalidArchetypeIDException(id);
        }
    }
}
