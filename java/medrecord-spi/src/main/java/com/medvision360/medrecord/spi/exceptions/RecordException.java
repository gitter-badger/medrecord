package com.medvision360.medrecord.spi.exceptions;

public class RecordException extends Exception {
    public RecordException() {
    }

    public RecordException(String message) {
        super(message);
    }

    public RecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordException(Throwable cause) {
        super(cause);
    }

    public RecordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
