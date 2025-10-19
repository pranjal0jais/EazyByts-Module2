package com.pranjal.exception;

public class StockDoesNotExistException extends RuntimeException{
    public StockDoesNotExistException(String message) {
        super(message);
    }
}
