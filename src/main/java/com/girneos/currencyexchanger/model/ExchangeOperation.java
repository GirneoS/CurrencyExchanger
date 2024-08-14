package com.girneos.currencyexchanger.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ExchangeOperation {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;


    public ExchangeOperation(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount.setScale(2, RoundingMode.DOWN);
    }

}
