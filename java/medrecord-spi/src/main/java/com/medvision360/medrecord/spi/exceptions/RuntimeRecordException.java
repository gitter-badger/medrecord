package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class RuntimeRecordException extends RuntimeException
{
    private static final long serialVersionUID = 0x130L;
    
    public RuntimeRecordException()
    {
    }

    public RuntimeRecordException(String message)
    {
        super(message);
    }

    public RuntimeRecordException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RuntimeRecordException(Throwable cause)
    {
        super(cause);
    }

    public RuntimeRecordException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
