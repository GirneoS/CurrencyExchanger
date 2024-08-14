package com.girneos.currencyexchanger.utils;

import java.math.BigDecimal;

public class Utils {
    public static BigDecimal parseBigDecimal(String rate){
        try{
            return new BigDecimal(rate);
        }catch (NumberFormatException e){
            throw e;
        }
    }
}
