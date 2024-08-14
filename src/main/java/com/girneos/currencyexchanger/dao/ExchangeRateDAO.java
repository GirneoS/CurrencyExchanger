package com.girneos.currencyexchanger.dao;

import com.girneos.currencyexchanger.model.Currency;
import com.girneos.currencyexchanger.model.ExchangeRate;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;
import com.girneos.currencyexchanger.service.CurrencyService;
import com.girneos.currencyexchanger.utils.Utils;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;

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
                BigDecimal rate = Utils.parseBigDecimal(resultSet.getString("Rate"));


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

    public ExchangeRate get(String baseCurrencyCode, String targetCurrencyCode) throws SQLException, ClassNotFoundException {
        String query = "SELECT rates.id, rates.rate, c.code as baseCode, c2.code as targetCode FROM ExchangeRates as rates " +
                "JOIN Currencies as c on rates.BaseCurrencyId = c.ID " +
                "JOIN Currencies as c2 on rates.TargetCurrencyId = c2.ID " +
                "WHERE c.code=? AND c2.code=?";


        try(Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1,baseCurrencyCode);
            statement.setString(2,targetCurrencyCode);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            CurrencyService currencyService = new CurrencyService();

            if(resultSet.getString("baseCode")!=null && resultSet.getString("targetCode")!=null) {
                Currency baseCurrency = currencyService.get(resultSet.getString("baseCode"));
                Currency targetCurrency = currencyService.get(resultSet.getString("targetCode"));
                BigDecimal rate = resultSet.getBigDecimal("rate");


                ExchangeRate exchangeRate =  new ExchangeRate(baseCurrency, targetCurrency, rate);
                exchangeRate.setID(resultSet.getInt("ID"));
                return exchangeRate;
            }
            return null;
        } catch (NoSuchCurrencyException e) {
            throw new RuntimeException(e);
        }
    }
    public ExchangeRate update(ExchangeRate exchangeRate, BigDecimal rate) throws SQLException {
        ExchangeRate updatedExchangeRate = null;
        try (Connection connection = DriverManager.getConnection(url)) {

            String query = "UPDATE ExchangeRates SET Rate=? WHERE ID=?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setBigDecimal(1, rate);
            statement.setInt(2, exchangeRate.getID());

            if (statement.executeUpdate() > 0) {
                exchangeRate.setRate(rate);
                updatedExchangeRate = exchangeRate;
            } else {
                String reverseQuery = "UPDATE ExchangeRates SET Rate=? " +
                        "WHERE BaseCurrencyId=? AND TargetCurrencyId=?";
                PreparedStatement reverseStatement = connection.prepareStatement(reverseQuery);

                reverseStatement.setBigDecimal(1, BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_EVEN));
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
            BigDecimal rate = exchangeRate.getRate();


            String query = "INSERT INTO ExchangeRates " +
                    "VALUES (?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            statement.setInt(2, baseCurrencyID);
            statement.setInt(3, targetCurrencyID);
            statement.setBigDecimal(4, rate);

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
