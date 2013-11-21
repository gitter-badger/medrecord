package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "DISPOSAL_EXCEPTION",
        message = "Problem shutting down server: {0}"
)
public class DisposalException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public DisposalException()
    {
    }

    public DisposalException(String message)
    {
        super(message);
    }

    public DisposalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DisposalException(Throwable cause)
    {
        super(cause);
    }

    public DisposalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DisposalException(Collection<String> arguments)
    {
        super(arguments);
    }
}
