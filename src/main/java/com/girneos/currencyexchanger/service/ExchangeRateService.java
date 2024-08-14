package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;
import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public ExchangeRate get(String baseCurrencyCode, String targetCurrencyCode) throws SQLException, ClassNotFoundException, NoSuchExchangeRateException {
        ExchangeRate exchangeRate = exchangeRateDAO.get(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate==null) {
            exchangeRate = exchangeRateDAO.get(targetCurrencyCode, baseCurrencyCode);
            if (exchangeRate!=null) {
                //если получилось найти обратную валютную пару, то создаем нужную на ее основе
                Currency baseCurrency = exchangeRate.getTargetCurrency();
                Currency targetCurrency = exchangeRate.getBaseCurrency();
                BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_EVEN);
                return new ExchangeRate(baseCurrency, targetCurrency, rate);
            }
        }
        if (exchangeRate==null)
            throw new NoSuchExchangeRateException();
        return exchangeRate;
    }

    public ExchangeRate update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException, ClassNotFoundException, NoSuchExchangeRateException {
        ExchangeRate exchangeRate = exchangeRateDAO.get(baseCurrencyCode, targetCurrencyCode);

        if(exchangeRate == null)
            throw new NoSuchExchangeRateException();
        else{
            exchangeRateDAO.update(exchangeRate, rate);
        }
        return exchangeRate;

    }

    public ExchangeRate save(String baseCode, String targetCode, BigDecimal rate) throws SQLException, ClassNotFoundException, NoSuchCurrencyException {
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
