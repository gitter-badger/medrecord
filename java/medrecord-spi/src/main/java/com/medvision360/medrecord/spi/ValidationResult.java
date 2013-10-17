/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

public interface ValidationResult
{
    public boolean isValid();

    public long getLineNumber();

    public long getRowNumber();

    public String getMessage();

    public String getProblematicFragment();

    public String getPath();

    public Exception getDetails();
}
