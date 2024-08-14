package com.girneos.currencyexchanger.model;

import lombok.Data;

@Data
public class Currency {
    private int id;
    private String code;
    private String name;
    private String sign;

    public Currency(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "id=" + id + "; code=" + code;
    }
}
