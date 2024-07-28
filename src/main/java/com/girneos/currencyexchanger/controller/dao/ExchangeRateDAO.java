package com.ozhegov.currencyexchanger.controller.dao;

import com.ozhegov.currencyexchanger.model.Currency;
import com.ozhegov.currencyexchanger.model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO implements DAO<ExchangeRate>{
    private final String url = "jdbc:sqlite:/Users/mak/IdeaProjects/CurrencyExchanger/src/main/resources/my-database.db";
    public ExchangeRateDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException e ){
            e.printStackTrace();
        }
    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> listOfRates = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ExchangeRates");

            while(resultSet.next()){
                int id = resultSet.getInt("ID");
                int baseCurrencyID = resultSet.getInt("BaseCurrencyID");
                int targetCurrencyID = resultSet.getInt("TargetCurrencyID");
                double rate = resultSet.getDouble("Rate");


                List<Currency> listOfCurrencies = findAllCurrencies();

                Currency baseCurrency = listOfCurrencies.stream()
                        .filter(c->c.getId()==baseCurrencyID)
                        .findFirst()
                        .orElse(null);

                Currency targetCurrency = listOfCurrencies.stream()
                        .filter(c->c.getId()==targetCurrencyID)
                        .findFirst()
                        .orElse(null);

                ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

                exchangeRate.setID(id);
                listOfRates.add(exchangeRate);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return listOfRates;
    }

    @Override
    public ExchangeRate get(String reqCode) {
        return null;
    }

    @Override
    public void update(ExchangeRate exchangeRate, double rate) {

    }

    @Override
    public boolean save(ExchangeRate exchangeRate) {

    }

    @Override
    public int getLastId() {
        int lastID = -2;
        try(Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT MAX(rowid) AS last_ID from ExchangeRates");
            resultSet.next();

            lastID = resultSet.getInt("last_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastID;
    }
}
