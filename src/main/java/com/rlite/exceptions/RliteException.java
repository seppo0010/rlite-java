package com.rlite.exceptions;

public class RliteException extends RuntimeException {
    private static final long serialVersionUID = -2946266495682282678L;

    public RliteException(String message) {
        super(message);
    }

    public RliteException(Throwable e) {
        super(e);
    }

    public RliteException(String message, Throwable cause) {
        super(message, cause);
    }
}
