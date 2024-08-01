package com.girneos.currencyexchanger.dao;

import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeRate;
import com.girneos.currencyexchanger.service.CurrencyService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {
    private final String url = "jdbc:sqlite:/Users/mak/IdeaProjects/CurrencyExchanger/src/main/resources/my-database.db";

    public ExchangeRateDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    public List<ExchangeRate> getAll() throws SQLException, ClassNotFoundException {
        List<ExchangeRate> listOfRates = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ExchangeRates");

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int baseCurrencyID = resultSet.getInt("BaseCurrencyID");
                int targetCurrencyID = resultSet.getInt("TargetCurrencyID");
                double rate = resultSet.getDouble("Rate");


                CurrencyService service = new CurrencyService();
                List<Currency> listOfCurrencies = service.getAll();

                Currency baseCurrency = listOfCurrencies.stream()
                        .filter(c -> c.getId() == baseCurrencyID)
                        .findFirst()
                        .orElse(null);

                Currency targetCurrency = listOfCurrencies.stream()
                        .filter(c -> c.getId() == targetCurrencyID)
                        .findFirst()
                        .orElse(null);

                ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

                exchangeRate.setID(id);
                listOfRates.add(exchangeRate);
            }

        }

        return listOfRates;
    }

    public ExchangeRate get(String reqCode) throws SQLException, ClassNotFoundException {
        List<ExchangeRate> rateList = getAll();

        ExchangeRate rate = rateList.stream()
                .filter(r -> r.toString().equals(reqCode))
                .findAny()
                .orElse(null);

        if (rate == null) {
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

    public ExchangeRate update(ExchangeRate exchangeRate, double rate) throws SQLException {
        ExchangeRate updatedExchangeRate = null;
        try (Connection connection = DriverManager.getConnection(url)) {

            String query = "UPDATE ExchangeRates SET Rate=? WHERE ID=?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setDouble(1, rate);
            statement.setInt(2, exchangeRate.getID());

            if (statement.executeUpdate() > 0) {
                exchangeRate.setRate(rate);
                updatedExchangeRate = exchangeRate;
            } else {
                String reverseQuery = "UPDATE ExchangeRates SET Rate=? WHERE BaseCurrencyId=? AND TargetCurrencyId=?";
                PreparedStatement reverseStatement = connection.prepareStatement(reverseQuery);

                reverseStatement.setDouble(1, Math.pow(rate, -1));
                reverseStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
                reverseStatement.setInt(3, exchangeRate.getBaseCurrency().getId());

                if (reverseStatement.executeUpdate() > 0) {
                    exchangeRate.setRate(rate);
                    updatedExchangeRate = exchangeRate;
                }
            }
        }

        return updatedExchangeRate;
    }

    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url)) {

            exchangeRate.setID(getLastId() + 1);
            int id = exchangeRate.getID();
            int baseCurrencyID = exchangeRate.getBaseCurrency().getId();
            int targetCurrencyID = exchangeRate.getTargetCurrency().getId();
            double rate = exchangeRate.getRate();


            String query = "INSERT INTO ExchangeRates " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            statement.setInt(2, baseCurrencyID);
            statement.setInt(3, targetCurrencyID);
            statement.setDouble(4, rate);

            statement.executeUpdate();

            return exchangeRate;
        }
    }

    public int getLastId() throws SQLException {
        int lastID = -2;
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT MAX(rowid) AS last_ID from ExchangeRates");
            resultSet.next();

            lastID = resultSet.getInt("last_ID");
        }
        return lastID;
    }
}
