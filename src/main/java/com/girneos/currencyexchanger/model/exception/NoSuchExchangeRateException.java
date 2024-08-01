package com.girneos.currencyexchanger.model.exception;

public class NoSuchExchangeRateException extends Exception{
    public NoSuchExchangeRateException(String message) {
        super(message);
    }
}
