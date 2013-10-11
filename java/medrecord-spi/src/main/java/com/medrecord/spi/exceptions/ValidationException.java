package com.medrecord.spi.exceptions;

import com.medrecord.spi.ValidationReport;

public class ValidationException extends RecordException {
    private ValidationReport report;

    public ValidationException(ValidationReport report) {
        this.report = report;
    }

    public ValidationException(String message, ValidationReport report) {
        super(message);
        this.report = report;
    }

    public ValidationException(String message, Throwable cause, ValidationReport report) {
        super(message, cause);
        this.report = report;
    }

    public ValidationException(Throwable cause, ValidationReport report) {
        super(cause);
        this.report = report;
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                               ValidationReport report) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.report = report;
    }

    public ValidationReport getReport() {
        return report;
    }
}
