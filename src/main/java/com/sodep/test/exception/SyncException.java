package com.sodep.test.exception;

public sealed class SyncException extends RuntimeException {

    public SyncException(String message) {
        super(message);
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public static final class ApiUnreachableException extends SyncException {
        public ApiUnreachableException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static final class ApiAuthenticationException extends SyncException {
        public ApiAuthenticationException(String message) {
            super(message);
        }
    }

    public static final class DataIntegrityException extends SyncException {
        public DataIntegrityException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static final class ProcessingException extends SyncException {
        public ProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
