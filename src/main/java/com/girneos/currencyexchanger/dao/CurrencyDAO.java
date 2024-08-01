package com.girneos.currencyexchanger.dao;

import com.girneos.currencyexchanger.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private final String url = "jdbc:sqlite:/Users/mak/IdeaProjects/CurrencyExchanger/src/main/resources/my-database.db";
    public CurrencyDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    public List<Currency> getAll() throws SQLException {
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
        }

        return currencyList;
    }

    public Currency get(String reqCode) throws SQLException {
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
        }
        return reqCurrency;
    }

    public void save(Currency currency) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url)){

            currency.setId(getLastId()+1);

            int id = currency.getId();
            String code = currency.getCode();
            String fullName = currency.getName();
            String sign = currency.getSign();

            String query = "INSERT INTO Currencies VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1,id);
            statement.setString(2,code);
            statement.setString(3,fullName);
            statement.setString(4,sign);

            statement.executeUpdate();
        }
    }

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
