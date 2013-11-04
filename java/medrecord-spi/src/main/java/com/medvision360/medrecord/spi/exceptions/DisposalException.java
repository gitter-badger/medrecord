package com.medvision360.medrecord.spi.exceptions;

@SuppressWarnings("UnusedDeclaration")
public class DisposalException extends RecordException
{
    private static final long serialVersionUID = 0x130L;
    
    public DisposalException()
    {
    }

    public DisposalException(String message)
    {
        super(message);
    }

    public DisposalException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DisposalException(Throwable cause)
    {
        super(cause);
    }

    public DisposalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
