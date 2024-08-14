package com.girneos.currencyexchanger.model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ExchangeRate {
    private int ID;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }


    @Override
    public String toString() {
        return baseCurrency.getCode() + targetCurrency.getCode();
    }
}
