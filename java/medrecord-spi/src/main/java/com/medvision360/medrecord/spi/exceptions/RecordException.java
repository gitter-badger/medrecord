/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class RecordException extends Exception
{
    private static final long serialVersionUID = 0x130L;

    public RecordException()
    {
    }

    public RecordException(String message)
    {
        super(message);
    }

    public RecordException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RecordException(Throwable cause)
    {
        super(cause);
    }

    public RecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
