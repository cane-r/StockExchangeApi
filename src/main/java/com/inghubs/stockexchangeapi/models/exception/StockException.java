package com.inghubs.stockexchangeapi.models.exception;

public class StockException extends RuntimeException {
    public StockException(String msg) {
        super(msg);
    }
}
