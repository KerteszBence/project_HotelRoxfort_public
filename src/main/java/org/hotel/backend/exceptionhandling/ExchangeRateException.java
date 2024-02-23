package org.hotel.backend.exceptionhandling;



public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}