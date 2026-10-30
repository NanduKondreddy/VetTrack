package com.vettrack.exception;

public class BatchExpiredException extends RuntimeException {
    public BatchExpiredException(String message) {
        super(message);
    }
}
