package com.medvision360.medrecord.spi.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "PARSE_EXCEPTION",
        message = "Problem parsing the resource: {0}"
)
public class ParseException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public ParseException()
    {
    }

    public ParseException(String message)
    {
        super(message);
    }

    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ParseException(Throwable cause)
    {
        super(cause);
    }

    public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ParseException(Collection<String> arguments)
    {
        super(arguments);
    }
}
