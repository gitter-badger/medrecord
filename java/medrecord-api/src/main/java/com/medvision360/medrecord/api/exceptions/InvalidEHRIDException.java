package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_EHR_ID_EXCEPTION",
        message = "Not a valid EHR ID: {0}"
)
public class InvalidEHRIDException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidEHRIDException()
    {
        super();
    }

    public InvalidEHRIDException(String message)
    {
        super(message);
    }

    public InvalidEHRIDException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidEHRIDException(Throwable cause)
    {
        super(cause);
    }

    public InvalidEHRIDException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidEHRIDException(Collection<String> arguments)
    {
        super(arguments);
    }
}
