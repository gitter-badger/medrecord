package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "IO_RECORD_EXCEPTION",
        message = "Generic IO error in server: {0}"
)
public class IORecordException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public IORecordException()
    {
    }

    public IORecordException(String message)
    {
        super(message);
    }

    public IORecordException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IORecordException(Throwable cause)
    {
        super(cause);
    }

    public IORecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IORecordException(Collection<String> arguments)
    {
        super(arguments);
    }
}
