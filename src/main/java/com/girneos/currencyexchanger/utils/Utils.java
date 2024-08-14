package com.girneos.currencyexchanger.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public class Utils {
    public static BigDecimal parseBigDecimal(String rate){
        try{
            return new BigDecimal(rate);
        }catch (NumberFormatException e){
            throw e;
        }
    }


    public static boolean isValidExchangeArgs(String from, String to, String amount){
        if(from==null || to==null || amount==null)
            return false;
        if(from.isEmpty() || to.isEmpty() || amount.isEmpty())
            return false;
        if(from.length()!=3 || to.length()!=3)
            return false;
        return true;
    }
    public static boolean isValidExchangeRateArgs(String baseCode, String targetCode, String rate){
        if(baseCode==null || targetCode==null || rate==null)
            return false;
        if(baseCode.isEmpty() || targetCode.isEmpty() || rate.isEmpty())
            return false;
        if(baseCode.length()!=3 || targetCode.length()!=3)
            return false;

        return true;
    }
    public static boolean isValidCurrencyArgs(String code, String name, String sign){
        if(code==null || name==null || sign==null)
            return false;
        if(code.isEmpty() || name.isEmpty() || sign.isEmpty())
            return false;
        if(code.length()!=3 || name.length()>30 || sign.length()>5)
            return false;

        return true;
    }
}
