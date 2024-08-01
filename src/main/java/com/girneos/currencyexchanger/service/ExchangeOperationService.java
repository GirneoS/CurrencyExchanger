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
    private ExchangeRateDAO exchangeRateDAO;

    public ExchangeOperation makeExchange(String from, String to, double amount) throws ClassNotFoundException, SQLException, NoSuchExchangeRateException {
        currencyDAO = new CurrencyDAO();
        exchangeRateDAO = new ExchangeRateDAO();

        Currency baseCurrency = currencyDAO.get(from);
        Currency targetCurrency = currencyDAO.get(to);

        ExchangeRate exchangeRate = exchangeRateDAO.get(from + to);
        BigDecimal convertedAmount;
        if (exchangeRate == null) {
            ExchangeRate baseExchangeRate = exchangeRateDAO.get(from + "USD");
            ExchangeRate targetExchangeRate = exchangeRateDAO.get("USD" + to);

            if (baseExchangeRate == null || targetExchangeRate == null) {
                throw new NoSuchExchangeRateException("Невозможно совершить обмен между указанными валютами");
            }
            convertedAmount = BigDecimal.valueOf(amount * baseExchangeRate.getRate() * targetExchangeRate.getRate());
        } else {
            convertedAmount = BigDecimal.valueOf(amount * exchangeRate.getRate());
        }

        ExchangeOperation exchangeOperation = new ExchangeOperation(baseCurrency, targetCurrency, convertedAmount.doubleValue()/amount, amount, convertedAmount);

        return exchangeOperation;

    }

}
