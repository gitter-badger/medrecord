/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.CLIENT,
        code    = "INVALID_QUERY_EXCEPTION",
        message = "Not a valid query: {0}"
)
public class InvalidQueryException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InvalidQueryException()
    {
        super();
    }

    public InvalidQueryException(String message)
    {
        super(message);
    }

    public InvalidQueryException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidQueryException(Throwable cause)
    {
        super(cause);
    }

    public InvalidQueryException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidQueryException(Collection<String> arguments)
    {
        super(arguments);
    }
}
