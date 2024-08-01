package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.dao.DAO;
import com.girneos.currencyexchanger.model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final DAO<Currency> currencyDao = new CurrencyDAO();

    public CurrencyService() throws ClassNotFoundException {
    }

    public List<Currency> getAll() throws SQLException {

        return currencyDao.getAll();
    }

    public Currency get(String code) throws SQLException{
        return currencyDao.get(code);
    }

    public boolean save(Currency currency) throws SQLException {
        return currencyDao.save(currency);
    }


}
