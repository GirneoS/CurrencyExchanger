package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDao;

    public CurrencyService() throws ClassNotFoundException {
        currencyDao = new CurrencyDAO();
    }

    public List<Currency> getAll() throws SQLException {
        return currencyDao.getAll();
    }

    public Currency get(String code) throws SQLException, NoSuchCurrencyException {
        Currency currency = currencyDao.get(code);

        if (currency==null)
            throw new NoSuchCurrencyException("Валюты с таким кодом не существует в БД");

        return currencyDao.get(code);
    }

    public void save(Currency currency) throws SQLException {
        currencyDao.save(currency);
    }


}
