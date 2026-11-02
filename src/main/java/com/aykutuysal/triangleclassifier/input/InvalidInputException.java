package com.aykutuysal.triangleclassifier.input;

public final class InvalidInputException extends IllegalArgumentException {

    public InvalidInputException(final String message) {
        super(message);
    }

    public InvalidInputException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
