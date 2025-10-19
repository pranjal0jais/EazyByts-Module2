package com.pranjal.exception;

public class StockSymbolNotFoundException extends RuntimeException{
    public StockSymbolNotFoundException(String message) {
        super(message);
    }
}
