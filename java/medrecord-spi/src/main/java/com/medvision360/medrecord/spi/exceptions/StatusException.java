/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "STATUS_EXCEPTION",
        message = "Problem determining server status: {0}"
)
public class StatusException extends RecordException
{
    private static final long serialVersionUID = 0x130L;

    public StatusException()
    {
    }

    public StatusException(String message)
    {
        super(message);
    }

    public StatusException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public StatusException(Throwable cause)
    {
        super(cause);
    }

    public StatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StatusException(Collection<String> arguments)
    {
        super(arguments);
    }
}
