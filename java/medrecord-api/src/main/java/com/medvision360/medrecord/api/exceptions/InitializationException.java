package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "INITIALIZATION_EXCEPTION",
        message = "Problem starting up server: {0}"
)
public class InitializationException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InitializationException()
    {
    }

    public InitializationException(String message)
    {
        super(message);
    }

    public InitializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InitializationException(Throwable cause)
    {
        super(cause);
    }

    public InitializationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InitializationException(Collection<String> arguments)
    {
        super(arguments);
    }
}
