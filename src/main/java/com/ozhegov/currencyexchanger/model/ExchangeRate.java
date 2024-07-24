package com.ozhegov.currencyexchanger.model;

import javax.xml.crypto.Data;

public class ExchangeRate {
    private int ID;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return baseCurrency.getCode() + targetCurrency.getCode();
    }
}
