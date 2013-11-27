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
        status  = 404,
        cause   = Cause.CLIENT,
        code    = "LOCATABLE_HAS_NO_EHR_EXCEPTION",
        message = "Locatable has no EHR: {0}"
)
public class LocatableHasNoEHRException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public LocatableHasNoEHRException()
    {
        super();
    }

    public LocatableHasNoEHRException(String message)
    {
        super(message);
    }

    public LocatableHasNoEHRException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LocatableHasNoEHRException(Throwable cause)
    {
        super(cause);
    }

    public LocatableHasNoEHRException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LocatableHasNoEHRException(Collection<String> arguments)
    {
        super(arguments);
    }
}
