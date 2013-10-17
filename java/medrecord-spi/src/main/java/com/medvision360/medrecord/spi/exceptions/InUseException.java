package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class InUseException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public InUseException()
    {
        super();
    }

    public InUseException(String message)
    {
        super(message);
    }

    public InUseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InUseException(Throwable cause)
    {
        super(cause);
    }

    public InUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
