package com.zorggemak.commons;

@SuppressWarnings("UnusedDeclaration")
public class NoSystemIdAuditException extends AuditException {
    public NoSystemIdAuditException() {
        super("Audit problem: no system id");
    }

    public NoSystemIdAuditException(String message) {
        super(message);
    }

    public NoSystemIdAuditException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSystemIdAuditException(Throwable cause) {
        super(cause);
    }

    public NoSystemIdAuditException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
