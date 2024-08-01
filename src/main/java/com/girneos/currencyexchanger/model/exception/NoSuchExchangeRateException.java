package com.girneos.currencyexchanger.exception;

public class NoSuchExchangeRateException extends Exception{
    public NoSuchExchangeRateException(String message) {
        super(message);
    }
}
