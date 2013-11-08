package com.medvision360.medrecord.api.archetype;

import java.util.List;

import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

// simple exception thrown when the archetype was not found, the @ApiException annotation contains all the
// information about the error and is included in the api documentation

@ApiException(
        // HTTP status
        status=404,

        // most likely cause of the error
        cause= Cause.CLIENT,

        // unique code to distinguish the error at the client side
        code="ARCHETYPE_NOT_FOUND",

        // message shown to the user, this message is to be localized and can contain placeholders
        message="The archetype with ID {0} was not found."
)
public class ArchetypeNotFoundException extends AnnotatedResourceException
{
    public ArchetypeNotFoundException(final String archetype)
    {
        super(null, archetype);
    }

    // do not use, only for deserializing
    public ArchetypeNotFoundException(final List<String> arguments)
    {
        super(null, arguments);
    }
}
