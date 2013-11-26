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
        status  = 404, // considered 410, but given we do have un-delete, 404 seems more appropriate
        cause   = Cause.CLIENT,
        code    = "DELETED_EXCEPTION",
        message = "Resource deleted: {0}"
)
public class DeletedException extends NotFoundException
{
    private static final long serialVersionUID = 0x130L;
    
    private Object m_deleted;

    public DeletedException(Object deleted)
    {
        m_deleted = deleted;
    }

    public DeletedException(String message, Object deleted)
    {
        super(message);
        m_deleted = deleted;
    }

    public DeletedException(String message, Throwable cause, Object deleted)
    {
        super(message, cause);
        m_deleted = deleted;
    }

    public DeletedException(Throwable cause, Object deleted)
    {
        super(cause);
        m_deleted = deleted;
    }

    public DeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
            Object deleted)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        m_deleted = deleted;
    }

    public DeletedException(Collection<String> arguments, Object deleted)
    {
        super(arguments);
        m_deleted = deleted;
    }

    public Object getDeleted()
    {
        return m_deleted;
    }
}
