package com.vettrack.exception;

public class BatchOutOfStockException extends RuntimeException {
    public BatchOutOfStockException(String message) {
        super(message);
    }
}
