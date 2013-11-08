package com.zorggemak.commons;

@SuppressWarnings("UnusedDeclaration")
public class NoUserIdAuditException extends AuditException {
    public NoUserIdAuditException() {
        super("Audit problem: no user id");
    }

    public NoUserIdAuditException(String message) {
        super(message);
    }

    public NoUserIdAuditException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoUserIdAuditException(Throwable cause) {
        super(cause);
    }

    public NoUserIdAuditException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
