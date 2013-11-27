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
import com.medvision360.medrecord.api.ValidationReport;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 400,
        cause   = Cause.SERVER,
        code    = "VALIDATION_EXCEPTION",
        message = "Problem validating resource: {0}"
)
public class ValidationException extends RecordException
{
    private static final long serialVersionUID = 0x130L;

    private ValidationReport m_report;

    public ValidationException()
    {
        super();
    }

    public ValidationException(String message)
    {
        super(message);
    }

    public ValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ValidationException(Throwable cause)
    {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidationException(ValidationReport report)
    {
        this.m_report = report;
    }

    public ValidationException(String message, ValidationReport report)
    {
        super(message);
        m_report = report;
    }

    public ValidationException(String message, Throwable cause, ValidationReport report)
    {
        super(message, cause);
        m_report = report;
    }

    public ValidationException(Throwable cause, ValidationReport report)
    {
        super(cause);
        m_report = report;
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
            ValidationReport report)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        m_report = report;
    }

    public ValidationException(Collection<String> arguments)
    {
        super(arguments);
    }

    public ValidationReport getReport()
    {
        return m_report;
    }
}
