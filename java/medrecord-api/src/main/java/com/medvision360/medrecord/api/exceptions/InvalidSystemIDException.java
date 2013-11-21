package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_SYSTEM_ID_EXCEPTION",
        message = "Not a valid system ID: {0}"
)
public class InvalidSystemIDException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidSystemIDException()
    {
        super();
    }

    public InvalidSystemIDException(String message)
    {
        super(message);
    }

    public InvalidSystemIDException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidSystemIDException(Throwable cause)
    {
        super(cause);
    }

    public InvalidSystemIDException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidSystemIDException(Collection<String> arguments)
    {
        super(arguments);
    }
}
