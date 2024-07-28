package com.girneos.currencyexchanger.model.dto;

import com.girneos.currencyexchanger.model.Currency;

public class ExchangeOperationDTO {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;


    public ExchangeOperationDTO(Currency baseCurrency, Currency targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }
}
