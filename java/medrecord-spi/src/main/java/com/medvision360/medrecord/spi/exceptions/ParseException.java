package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class ParseException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public ParseException()
    {
    }

    public ParseException(String message)
    {
        super(message);
    }

    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ParseException(Throwable cause)
    {
        super(cause);
    }

    public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
