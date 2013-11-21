package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "PATTERN_EXCEPTION",
        message = "Invalid regular expression: {0}"
)
public class PatternException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public PatternException()
    {
    }

    public PatternException(String message)
    {
        super(message);
    }

    public PatternException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PatternException(Throwable cause)
    {
        super(cause);
    }

    public PatternException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PatternException(Collection<String> arguments)
    {
        super(arguments);
    }
}
