package com.girneos.currencyexchanger.model.exception;

public class ExchangeRateAlreadyExistsException extends Exception{
    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
}
