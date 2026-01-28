package com.sodep.test.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SyncException.class)
    public ResponseEntity<ErrorResponse> handleSyncException(SyncException ex,
                                                             HttpServletRequest request) {
        return switch (ex) {
            case SyncException.ApiUnreachableException e -> {
                log.error("API unreachable: {}", e.getMessage(), e);
                yield buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                        e.getMessage(), request);
            }
            case SyncException.ApiAuthenticationException e -> {
                log.error("API authentication failed: {}", e.getMessage());
                yield buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                        e.getMessage(), request);
            }
            case SyncException.DataIntegrityException e -> {
                log.error("Data integrity error: {}", e.getMessage(), e);
                yield buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity",
                        e.getMessage(), request);
            }
            case SyncException.ProcessingException e -> {
                log.error("Processing error: {}", e.getMessage(), e);
                yield buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        e.getMessage(), request);
            }
            default -> {
                log.error("Unknown sync error: {}", ex.getMessage(), ex);
                yield buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        "An unknown error occurred", request);
            }
        };
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(HttpClientErrorException ex,
                                                               HttpServletRequest request) {
        log.warn("HTTP client error: {}", ex.getMessage());
        var status = HttpStatus.valueOf(ex.getStatusCode().value());
        return buildResponse(status, status.getReasonPhrase(), ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error,
                                                        String message,
                                                        HttpServletRequest request) {
        var body = new ErrorResponse(status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
