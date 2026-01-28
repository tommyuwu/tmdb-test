package com.sodep.test.exception;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, Instant.now(), path);
    }
}
