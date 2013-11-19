package com.medvision360.medrecord.spi.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "TRANSFORM_EXCEPTION",
        message = "Problem transforming resource: {0}"
)
public class TransformException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public TransformException()
    {
    }

    public TransformException(String message)
    {
        super(message);
    }

    public TransformException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TransformException(Throwable cause)
    {
        super(cause);
    }

    public TransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransformException(Collection<String> arguments)
    {
        super(arguments);
    }
}
