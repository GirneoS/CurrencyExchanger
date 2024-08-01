package com.girneos.currencyexchanger.exception;

public class ExchangeRateAlreadyExistsException extends Exception{
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
}
