package com.aykutuysal.triangleclassifier.domain;

public final class InvalidTriangleException extends IllegalArgumentException {

    public InvalidTriangleException(final String message) {
        super(message);
    }
}
