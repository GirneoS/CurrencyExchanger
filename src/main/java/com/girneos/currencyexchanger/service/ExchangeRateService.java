package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;
import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private ExchangeRateDAO exchangeRateDAO;

    public ExchangeRateService() throws ClassNotFoundException {
        this.exchangeRateDAO = new ExchangeRateDAO();
    }
    public List<ExchangeRate> getAll() throws SQLException, ClassNotFoundException {
        return exchangeRateDAO.getAll();
    }
    public ExchangeRate get(String code) throws SQLException, ClassNotFoundException, NoSuchExchangeRateException {
        ExchangeRate exchangeRate = exchangeRateDAO.get(code);
        if (exchangeRate==null)
            throw new NoSuchExchangeRateException("Обменный курс для пары не найден");

        return exchangeRateDAO.get(code);
    }

    public ExchangeRate update(String code, double rate) throws SQLException, ClassNotFoundException, NoSuchExchangeRateException {
        ExchangeRate exchangeRate = exchangeRateDAO.get(code);

        if(exchangeRate == null)
            throw new NoSuchExchangeRateException("Указанная валютная пара не найдена");
        else{
            exchangeRateDAO.update(exchangeRate, rate);
        }
        return exchangeRate;

    }

    public ExchangeRate save(String baseCode, String targetCode, double rate) throws SQLException, ClassNotFoundException, NoSuchCurrencyException {
        CurrencyService currencyService = new CurrencyService();

        Currency baseCurrency = currencyService.get(baseCode);
        Currency targetCurrency = currencyService.get(targetCode);

        if(baseCurrency == null || targetCurrency == null){
            throw new NoSuchCurrencyException("Одна(или две) валюта из пары не существует в БД");
        }else {
            ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

            return exchangeRateDAO.save(exchangeRate);
        }
    }
}
