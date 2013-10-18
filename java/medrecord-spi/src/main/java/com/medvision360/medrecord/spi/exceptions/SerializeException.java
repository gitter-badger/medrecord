package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class SerializeException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public SerializeException()
    {
    }

    public SerializeException(String message)
    {
        super(message);
    }

    public SerializeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SerializeException(Throwable cause)
    {
        super(cause);
    }

    public SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
