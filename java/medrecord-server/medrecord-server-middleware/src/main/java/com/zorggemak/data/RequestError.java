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
package com.zorggemak.data;

import com.zorggemak.commons.MiddlewareErrors;

import java.util.Date;

public class RequestError {
    private MiddlewareErrors error = MiddlewareErrors.OK;
    private String message = "";
    private Date theTime = null;
    private Exception exception;

    public RequestError(MiddlewareErrors error, String message, Exception exception) {
        this.error = error;
        this.message = message;
        this.exception = exception;
        this.theTime = new Date();
    }

    public int getErrorCode() {
        return (error.getErrorCode());
    }

    public String getErrorString() {
        return (error.getErrorString());
    }

    public String getErrorMessage() {
        return message;
    }

    public long getTimePassed() {
        return (System.currentTimeMillis() - theTime.getTime());
    }

    public Exception getException() {
        return exception;
    }
}
