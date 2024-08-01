package com.girneos.currencyexchanger.controller.dao;

import com.girneos.currencyexchanger.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO implements DAO<Currency> {
    private final String url = "jdbc:sqlite:/Users/mak/IdeaProjects/CurrencyExchanger/src/main/resources/my-database.db";
    public CurrencyDAO() {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException e ){
            e.printStackTrace();
        }
    }

    @Override
    public List<Currency> getAll() {
        List<Currency> currencyList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url)) {


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies");

            while(resultSet.next()){
                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sign");

                Currency currency = new Currency(code, fullName, sign);
                currency.setId(id);

                currencyList.add(currency);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return currencyList;
    }

    @Override
    public Currency get(String reqCode) {
        Currency reqCurrency = null;
        try(Connection connection = DriverManager.getConnection(url)){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Currencies WHERE Code = ?");

            statement.setString(1, reqCode);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {

                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sign");

                reqCurrency = new Currency(code, fullName, sign);
                reqCurrency.setId(id);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return reqCurrency;
    }

    @Override
    public Currency update(Currency currency, double rate) {
        return null;
    }

    @Override
    public boolean save(Currency currency) {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(url)){

            String code = currency.getCode();
            String fullName = currency.getName();
            String sign = currency.getSign();

            String query = "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1,code);
            statement.setString(2,fullName);
            statement.setString(3,sign);


            currency.setId(getLastId()+1);
            if(statement.executeUpdate()>0){
                result = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int getLastId() {
        int lastID = -1;
        try(Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT MAX(rowid) AS last_ID from Currencies");

            resultSet.next();
            lastID = resultSet.getInt("last_ID");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lastID;
    }
}
