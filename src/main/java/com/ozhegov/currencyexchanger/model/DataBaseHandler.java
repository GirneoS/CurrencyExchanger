package com.ozhegov.currencyexchanger.model;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler {
    private String url = "jdbc:sqlite:/Users/mak/IdeaProjects/CurrencyExchanger/src/main/resources/my-database.db";
    private Connection connection;


    public DataBaseHandler() {
        try {
            Class.forName("org.sqlite.JDBC");

            this.connection = DriverManager.getConnection(url);
        }catch (SQLException | ClassNotFoundException e){
            System.out.println("\u001B[32m"+"Ошибка при подключении к БД!"+"\u001B[0m");
            e.printStackTrace();
        }
    }

    public List<Currency> findAllCurrencies(){

        List<Currency> list = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies");

            while(resultSet.next()){
                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sign");

                Currency currency = new Currency(id, code, fullName, sign);

                list.add(currency);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }
    public Currency findCurrencyByCode(String reqCode) {
        Currency reqCurrency = null;
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Currencies WHERE Code = ?");

            statement.setString(1, reqCode);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {

                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sign");

                reqCurrency = new Currency(id, code, fullName, sign);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return reqCurrency;
    }
    public boolean createNewCurrency(Currency currency){
        boolean result = false;
        try {

            String code = currency.getCode();
            String fullName = currency.getFullName();
            String sign = currency.getSign();

            String query = "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1,code);
            statement.setString(2,fullName);
            statement.setString(3,sign);

            if(statement.executeUpdate()>0){
                result = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    public List<ExchangeRate> findAllExchangeRates(){
        List<ExchangeRate> listOfRates = new ArrayList<>();

        try{
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

                listOfRates.add(exchangeRate);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return listOfRates;
    }
    public boolean insertExchangeRate(ExchangeRate exchangeRate) {
        boolean result = false;

        try {
            exchangeRate.setID(getLastExchangeID()+1);
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
    public int getLastExchangeID() {
        int lastID = -2;
        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT MAX(rowid) AS last_ID from ExchangeRates");
            resultSet.next();

            lastID = resultSet.getInt("last_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastID;
    }
    public ExchangeRate findExchangeRate(String nameRate) {
        List<ExchangeRate> rateList = findAllExchangeRates();

        ExchangeRate rate = rateList.stream()
                .filter(r -> r.toString().equals(nameRate))
                .findAny()
                .orElse(new ExchangeRate(new Currency(-1, "", "", ""), new Currency(-1, "", "", ""), 0));

        if (rate.getBaseCurrency().getId() == -1) {
            StringBuilder revNameRate = new StringBuilder();

            revNameRate.append(nameRate, 3, nameRate.length());
            revNameRate.append(nameRate, 0, 3);

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
}
