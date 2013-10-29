package com.medvision360.medrecord.itest;

import com.medvision360.medrecord.spi.exceptions.RecordException;

@SuppressWarnings("UnusedDeclaration")
public class GenerateException extends RecordException
{
    public GenerateException()
    {
    }

    public GenerateException(String message)
    {
        super(message);
    }

    public GenerateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GenerateException(Throwable cause)
    {
        super(cause);
    }

    public GenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
