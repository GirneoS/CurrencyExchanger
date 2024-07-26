package com.ozhegov.currencyexchanger.model;

public class Exchange extends ExchangeRate{
    private double amount;
    private double convertedAmount;

    public Exchange(Currency baseCurrency, Currency targetCurrency, double rate, double amount, double convertedAmount) {
        super(baseCurrency, targetCurrency, rate);
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }
}
