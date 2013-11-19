package com.medvision360.medrecord.spi.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "SERIALIZE_EXCEPTION",
        message = "Problem serializing resource: {0}"
)
public class SerializeException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public SerializeException()
    {
    }

    public SerializeException(String message)
    {
        super(message);
    }

    public SerializeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SerializeException(Throwable cause)
    {
        super(cause);
    }

    public SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SerializeException(Collection<String> arguments)
    {
        super(arguments);
    }
}
