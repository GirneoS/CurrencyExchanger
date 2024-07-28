package com.girneos.currencyexchanger.controller.dao;

import java.util.List;

public interface DAO<T> {
    List<T> getAll();
    T get(String reqCode);
    T update(T t, double rate);
    boolean save(T t);
    int getLastId();

}
