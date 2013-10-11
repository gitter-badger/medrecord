package com.medrecord.spi.exceptions;

public class StatusException extends RecordException {
    public StatusException() {
    }

    public StatusException(String message) {
        super(message);
    }

    public StatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusException(Throwable cause) {
        super(cause);
    }

    public StatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
