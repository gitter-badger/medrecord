/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.api.exceptions;

import java.util.Collection;

import com.medvision360.lib.common.exceptions.ApiException;
import com.medvision360.lib.common.exceptions.Cause;

@SuppressWarnings("UnusedDeclaration")
@ApiException(
        status  = 500,
        cause   = Cause.SERVER,
        code    = "TRANSACTION_EXCEPTION",
        message = "Problem in transaction: {0}"
)
public class TransactionException extends RecordException
{
    private static final long serialVersionUID = 0x130L;

    public TransactionException()
    {
    }

    public TransactionException(String message)
    {
        super(message);
    }

    public TransactionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TransactionException(Throwable cause)
    {
        super(cause);
    }

    public TransactionException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransactionException(Collection<String> arguments)
    {
        super(arguments);
    }
}
