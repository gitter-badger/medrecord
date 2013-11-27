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

@SuppressWarnings("UnusedDeclaration")
public class RuntimeRecordException extends RuntimeException
{
    private static final long serialVersionUID = 0x130L;
    
    public RuntimeRecordException()
    {
    }

    public RuntimeRecordException(String message)
    {
        super(message);
    }

    public RuntimeRecordException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RuntimeRecordException(Throwable cause)
    {
        super(cause);
    }

    public RuntimeRecordException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
