package com.ozhegov.currencyexchanger.model;

public class Test {
    public static void main(String[] args) {
        DataBaseHandler handler = new DataBaseHandler();

        System.out.println(handler.findAllCurrencies());
    }
}
