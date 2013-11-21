package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_SUBJECT_ID_EXCEPTION",
        message = "Not a valid subject ID: {0}"
)
public class InvalidSubjectIDException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidSubjectIDException()
    {
        super();
    }

    public InvalidSubjectIDException(String message)
    {
        super(message);
    }

    public InvalidSubjectIDException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidSubjectIDException(Throwable cause)
    {
        super(cause);
    }

    public InvalidSubjectIDException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidSubjectIDException(Collection<String> arguments)
    {
        super(arguments);
    }
}
