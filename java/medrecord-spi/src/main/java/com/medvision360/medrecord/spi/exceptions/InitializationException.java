package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class InitializationException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InitializationException()
    {
    }

    public InitializationException(String message)
    {
        super(message);
    }

    public InitializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InitializationException(Throwable cause)
    {
        super(cause);
    }

    public InitializationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
