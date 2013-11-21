package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "UNSUPPORTED_QUERY_EXCEPTION",
        message = "Query not supported: {0}"
)
public class UnsupportedQueryException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public UnsupportedQueryException()
    {
        super();
    }

    public UnsupportedQueryException(String message)
    {
        super(message);
    }

    public UnsupportedQueryException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnsupportedQueryException(Throwable cause)
    {
        super(cause);
    }

    public UnsupportedQueryException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnsupportedQueryException(Collection<String> arguments)
    {
        super(arguments);
    }
}
