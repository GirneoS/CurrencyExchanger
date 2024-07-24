package com.ozhegov.currencyexchanger.model;

public class Currency {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "id="+id+"; code="+code;
    }
}
