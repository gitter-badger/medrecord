/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.api;

public interface ValidationReport
{
    public boolean isValid();

    public Iterable<ValidationResult> getReport();

    public Iterable<ValidationResult> getErrors();
}
