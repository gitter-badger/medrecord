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
package com.medvision360.medrecord.spi.exceptions;

import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.api.ValidationResult;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import com.medvision360.medrecord.spi.tck.ExceptionTestBase;

public class ValidationExceptionTest extends ExceptionTestBase<ValidationException>
{

    ValidationReport report = new ValidationReport()
    {
        @Override
        public boolean isValid()
        {
            return true;
        }

        @Override
        public Iterable<ValidationResult> getReport()
        {
            return null;
        }

        @Override
        public Iterable<ValidationResult> getErrors()
        {
            return null;
        }
    };

    @Override
    protected Class<ValidationException> getExceptionClass()
    {
        return ValidationException.class;
    }

    public void testAdditionalConstructors()
    {
        ValidationException e;
        e = new ValidationException(report);
        assertEquals(report, e.getReport());

        e = new ValidationException(msg, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));

        e = new ValidationException(msg, cause, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));
        assertEquals(cause, e.getCause());

        e = new ValidationException(msg, cause, true, true, report);
        assertEquals(report, e.getReport());
        assertTrue(e.getMessage().contains(msg));
        assertEquals(cause, e.getCause());
    }
}
