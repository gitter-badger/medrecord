package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
  status  = 400,
  cause   = Cause.CLIENT,
  code    = "MISSING_PARAMETER_EXCEPTION",
  message = "The required parameter is missing: {0}"
)
public class MissingParameterException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public MissingParameterException()
    {
    }

    public MissingParameterException(String message)
    {
        super(message);
    }

    public MissingParameterException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MissingParameterException(Throwable cause)
    {
        super(cause);
    }

    public MissingParameterException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MissingParameterException(Collection<String> arguments)
    {
        super(arguments);
    }
}
