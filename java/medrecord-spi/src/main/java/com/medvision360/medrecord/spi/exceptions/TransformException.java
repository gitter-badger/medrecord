package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class TransformException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public TransformException()
    {
    }

    public TransformException(String message)
    {
        super(message);
    }

    public TransformException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public TransformException(Throwable cause)
    {
        super(cause);
    }

    public TransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
