package com.ozhegov.currencyexchanger.controller;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.Currency;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import com.ozhegov.currencyexchanger.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/exchangeRates")
public class ExchangeRateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        DataBaseHandler handler = new DataBaseHandler();

        List<ExchangeRate> ratesList = handler.findAllExchangeRates();
        if(!ratesList.isEmpty()) {

            Gson gson = new Gson();
            String textJson = gson.toJson(ratesList);

            resp.getWriter().write(textJson);
        }else{
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Произошла ошибка на стороне сервера!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if(baseCode.isEmpty() || targetCode.isEmpty() || rate.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Отсутствует нужное поле формы");
        }

        DataBaseHandler handler = new DataBaseHandler();

        Currency baseCurrency = handler.findCurrencyByCode(baseCode);
        Currency targetCurrency = handler.findCurrencyByCode(targetCode);

        if(baseCurrency==null || targetCurrency==null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Одна (или обе) валюта из валютной пары не существует в БД");
        }else{

            if(handler.findExchangeRate(baseCode+targetCode)!=null){
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("Валютная пара с таким кодом уже существует");
            }else {

                ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, Double.parseDouble(rate));

                if (handler.insertExchangeRate(exchangeRate)) {
                    resp.setContentType("text/json");
                    Gson gson = new Gson();
                    String json = gson.toJson(exchangeRate);

                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(json);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("Ошибка");
                }
            }
        }
    }

}
