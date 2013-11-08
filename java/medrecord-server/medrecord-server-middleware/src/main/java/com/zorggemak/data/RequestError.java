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
