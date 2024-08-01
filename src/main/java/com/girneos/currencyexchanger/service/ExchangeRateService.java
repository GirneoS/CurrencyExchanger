package com.girneos.currencyexchanger.service;

import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
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
    public ExchangeRate get(String code) throws SQLException, ClassNotFoundException {
        return exchangeRateDAO.get(code);
    }

    public ExchangeRate update(ExchangeRate exchangeRate){

    }
}
