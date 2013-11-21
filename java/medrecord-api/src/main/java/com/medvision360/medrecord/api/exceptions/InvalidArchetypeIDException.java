package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_ARCHETYPE_ID_EXCEPTION",
        message = "Not a valid archetype ID: {0}"
)
public class InvalidArchetypeIDException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidArchetypeIDException()
    {
        super();
    }

    public InvalidArchetypeIDException(String message)
    {
        super(message);
    }

    public InvalidArchetypeIDException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidArchetypeIDException(Throwable cause)
    {
        super(cause);
    }

    public InvalidArchetypeIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidArchetypeIDException(Collection<String> arguments)
    {
        super(arguments);
    }
}
