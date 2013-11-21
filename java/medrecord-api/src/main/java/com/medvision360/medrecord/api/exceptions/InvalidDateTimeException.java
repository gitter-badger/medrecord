package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_DATE_TIME_ID_EXCEPTION",
        message = "Not a valid DateTime: {0}"
)
public class InvalidDateTimeException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidDateTimeException()
    {
        super();
    }

    public InvalidDateTimeException(String message)
    {
        super(message);
    }

    public InvalidDateTimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidDateTimeException(Throwable cause)
    {
        super(cause);
    }

    public InvalidDateTimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidDateTimeException(Collection<String> arguments)
    {
        super(arguments);
    }
}
