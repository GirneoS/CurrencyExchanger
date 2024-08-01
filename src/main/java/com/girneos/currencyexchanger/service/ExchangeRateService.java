package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.ExchangeRateDAO;

public class ExchangeRateService {
    private ExchangeRateDAO exchangeRateDAO;

    public ExchangeRateService(ExchangeRateDAO exchangeRateDAO) {
        this.exchangeRateDAO = new ExchangeRateDAO();
    }
}
