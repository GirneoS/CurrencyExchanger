package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeOperation;
import com.girneos.currencyexchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ExchangeOperationService {
    private CurrencyDAO currencyDAO;
    private ExchangeRateService exchangeRateService;

    public ExchangeOperation makeExchange(String from, String to, BigDecimal amount) throws ClassNotFoundException, SQLException, NoSuchExchangeRateException {
        currencyDAO = new CurrencyDAO();

        Currency baseCurrency = currencyDAO.get(from);
        Currency targetCurrency = currencyDAO.get(to);

        BigDecimal convertedAmount = amount.multiply(getRate(from,to));


        return new ExchangeOperation(baseCurrency, targetCurrency, convertedAmount.divide(amount), amount, convertedAmount);

    }
    //метод для поиска нужного rate при обмене
    private BigDecimal getRate(String from, String to) throws ClassNotFoundException, SQLException, NoSuchExchangeRateException {
        exchangeRateService = new ExchangeRateService();

        try {

            ExchangeRate exchangeRate = exchangeRateService.get(from, to);
            return exchangeRate.getRate();

        } catch (NoSuchExchangeRateException e) {
            ExchangeRate baseExchangeRate = exchangeRateService.get(from,"USD");
            ExchangeRate targetExchangeRate = exchangeRateService.get("USD",to);

            return baseExchangeRate.getRate().multiply(targetExchangeRate.getRate());
        }

    }


}
