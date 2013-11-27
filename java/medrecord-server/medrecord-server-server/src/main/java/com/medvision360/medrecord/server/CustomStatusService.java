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
package com.medvision360.medrecord.server;

import java.io.IOException;

import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.lib.server.service.JsonStatusService;
import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.AnnotatedUnsupportedOperationException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class CustomStatusService extends JsonStatusService
{
    @Override
    public final Status getStatus(
            Throwable throwable,
            final Request request,
            final Response response)
    {
        if (throwable instanceof IllegalArgumentException)
        {
            throwable = new AnnotatedIllegalArgumentException(throwable.getMessage(), throwable);
        }
        if (throwable instanceof UnsupportedOperationException)
        {
            throwable = new AnnotatedUnsupportedOperationException(throwable.getMessage(), throwable);
        }
        else if (
                throwable instanceof IOException ||
                throwable instanceof RuntimeRecordException)
        {
            throwable = toAnnotatedResourceException(throwable);
        }
        return super.getStatus(throwable, request, response);
    }

    private Throwable toAnnotatedResourceException(Throwable throwable)
    {
        Throwable cause = throwable.getCause();
        if (cause != null && cause instanceof AnnotatedResourceException)
        {
            throwable = cause;
        }
        else
        {
            throwable = new RecordException(
                    throwable.getClass().getSimpleName() + ": " + throwable.getMessage(), throwable);
        }
        return throwable;
    }
}
