package com.girneos.currencyexchanger.model.exception;

public class CurrencyAlreadyExistsException extends Exception{
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}
