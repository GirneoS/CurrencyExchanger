package com.girneos.currencyexchanger.controller.dao;

import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeRate;

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


                CurrencyDAO currencyDAO = new CurrencyDAO();
                List<Currency> listOfCurrencies = currencyDAO.getAll();

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
        List<ExchangeRate> rateList = getAll();

        ExchangeRate rate = rateList.stream()
                .filter(r -> r.toString().equals(reqCode))
                .findAny()
                .orElse(new ExchangeRate(new Currency("", "", ""), new Currency("", "", ""), 0));

        if (rate.getBaseCurrency().getCode().isEmpty()) {
            StringBuilder revNameRate = new StringBuilder();

            revNameRate.append(reqCode, 3, reqCode.length());
            revNameRate.append(reqCode, 0, 3);

            rate = rateList.stream()
                    .filter(r -> r.toString().contentEquals(revNameRate.toString()))
                    .findFirst()
                    .orElse(null);

            if (rate != null) {
                return new ExchangeRate(rate.getTargetCurrency(), rate.getBaseCurrency(), Math.pow(rate.getRate(), -1));
            }
        }

        return rate;
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate, double rate) {
        ExchangeRate updatedExchangeRate = null;
        try(Connection connection = DriverManager.getConnection(url)) {

            String query = "UPDATE ExchangeRates SET Rate=? WHERE ID=?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setDouble(1,rate);
            statement.setInt(2,exchangeRate.getID());

            if (statement.executeUpdate()>0){
                exchangeRate.setRate(rate);
                updatedExchangeRate = exchangeRate;
            }else{
                String reverseQuery = "UPDATE ExchangeRates SET Rate=? WHERE BaseCurrencyId=? AND TargetCurrencyId=?";
                PreparedStatement reverseStatement = connection.prepareStatement(reverseQuery);

                reverseStatement.setDouble(1,Math.pow(rate,-1));
                reverseStatement.setInt(2,exchangeRate.getTargetCurrency().getId());
                reverseStatement.setInt(3,exchangeRate.getBaseCurrency().getId());

                if(reverseStatement.executeUpdate()>0){
                    exchangeRate.setRate(rate);
                    updatedExchangeRate = exchangeRate;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return updatedExchangeRate;
    }

    @Override
    public boolean save(ExchangeRate exchangeRate) {
        boolean result = false;

        try(Connection connection = DriverManager.getConnection(url)) {

            exchangeRate.setID(getLastId()+1);
            int baseCurrencyID = exchangeRate.getBaseCurrency().getId();
            int targetCurrencyID = exchangeRate.getTargetCurrency().getId();
            double rate = exchangeRate.getRate();


            String query = "INSERT INTO ExchangeRates(BaseCurrencyID, TargetCurrencyID, Rate) " +
                    "VALUES (?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1,baseCurrencyID);
            statement.setInt(2,targetCurrencyID);
            statement.setDouble(3,rate);

            if(statement.executeUpdate()>0){
                result = true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
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
