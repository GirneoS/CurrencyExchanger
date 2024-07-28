package com.ozhegov.currencyexchanger.controller.dao;

import java.util.List;

public interface DAO<T> {
    List<T> getAll();
    T get(String reqCode);
    void update(T t, double rate);
    boolean save(T t);
    int getLastId();

}
