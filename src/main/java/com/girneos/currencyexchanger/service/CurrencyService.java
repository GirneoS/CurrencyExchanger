package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDAO currencyDao;

    public CurrencyService() throws ClassNotFoundException {
        currencyDao = new CurrencyDAO();
    }

    public List<Currency> getAll() throws SQLException {
        return currencyDao.getAll();
    }

    public Currency get(String code) throws SQLException, NoSuchCurrencyException {
        Optional<Currency> optionalCurrency = currencyDao.get(code);

        return optionalCurrency.orElseGet(() -> optionalCurrency.orElseThrow(NoSuchCurrencyException::new));
    }

    public void save(Currency currency) throws SQLException {
        currencyDao.save(currency);
    }


}
