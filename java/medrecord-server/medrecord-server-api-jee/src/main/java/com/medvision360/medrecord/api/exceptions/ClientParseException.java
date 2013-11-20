package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;
import com.medvision360.medrecord.spi.exceptions.ParseException;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "CLIENT_PARSE_EXCEPTION",
        message = "Problem parsing the resource: {0}"
)
public class ClientParseException extends ParseException
{
    private static final long serialVersionUID = 0x130L;
    
    public ClientParseException()
    {
    }

    public ClientParseException(String message)
    {
        super(message);
    }

    public ClientParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ClientParseException(Throwable cause)
    {
        super(cause);
    }

    public ClientParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ClientParseException(Collection<String> arguments)
    {
        super(arguments);
    }
}
