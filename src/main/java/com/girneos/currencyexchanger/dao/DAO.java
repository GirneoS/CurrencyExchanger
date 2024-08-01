package com.girneos.currencyexchanger.dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {
    List<T> getAll() throws SQLException, ClassNotFoundException;
    T get(String reqCode) throws SQLException, ClassNotFoundException;
    T update(T t, double rate) throws SQLException;
    boolean save(T t) throws SQLException;
    int getLastId() throws SQLException;

}
