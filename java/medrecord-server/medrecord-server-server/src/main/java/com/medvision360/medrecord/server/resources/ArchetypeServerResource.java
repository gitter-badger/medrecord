package com.medvision360.medrecord.server.resources;

import java.io.IOException;

import com.medvision360.lib.api.MissingParameterException;
import com.medvision360.lib.api.ServiceUnavailableException;
import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.medrecord.api.archetype.ArchetypeResource;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.openehr.rm.support.identification.ArchetypeID;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

public class ArchetypeServerResource
        extends AbstractServerResource
        implements ArchetypeResource
{
    @Override
    public String getArchetype() throws ServiceUnavailableException, MissingParameterException
    {
        // Any exception not derived from ResourceException or AnnotatedResourceException escaping this method will
        // result in a generic 500 exception. The client library generator currently does not handle any other checked
        // exception.

        try
        {
            String id = getQueryValue("id");
            if (id == null)
            {
                throw new MissingParameterException("id");
            }

            if ("test".equals(id))
            {
                // just for testing....
                return "lots of text";
            }

            if ("iae".equals(id))
            {
                // just for testing
                throw new IllegalArgumentException();
            }

            ArchetypeID archetypeID = new ArchetypeID(id);

            MedRecordEngine engine = engine();
            WrappedArchetype wrappedArchetype = null;
            wrappedArchetype = engine.getArchetypeStore().get(archetypeID);

            return wrappedArchetype.getAsString();
        }
        catch (NotFoundException e)
        {
            // this will result in a generic 404 exception
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Could not found it.", e);
        }
        catch (ParseException e)
        {
            // generic 500
            throw new ResourceException(e);
        }
        catch (IOException e)
        {
            // a 503
            throw new ServiceUnavailableException(e);
        }

        // IllegalArgumentException is not caught which means it will result in a generic 500 message,
        // if you want to do something with it you must convert it into an AnnotatedResourceException
    }
}
