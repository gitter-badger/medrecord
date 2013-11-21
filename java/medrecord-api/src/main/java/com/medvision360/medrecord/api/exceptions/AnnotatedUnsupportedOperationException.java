package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "UNSUPPORTED_OPERATION_EXCEPTION",
        message = "Unsupported operation: {0}"
)
public class AnnotatedUnsupportedOperationException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public AnnotatedUnsupportedOperationException()
    {
    }

    public AnnotatedUnsupportedOperationException(String message)
    {
        super(message);
    }

    public AnnotatedUnsupportedOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AnnotatedUnsupportedOperationException(Throwable cause)
    {
        super(cause);
    }

    public AnnotatedUnsupportedOperationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AnnotatedUnsupportedOperationException(Collection<String> arguments)
    {
        super(arguments);
    }
}
