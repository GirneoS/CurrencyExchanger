package com.girneos.currencyexchanger.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeOperation {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
    private double amount;
    private BigDecimal convertedAmount;


    public ExchangeOperation(Currency baseCurrency, Currency targetCurrency, double rate, double amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount.setScale(2, RoundingMode.DOWN);
    }

    public double getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
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
