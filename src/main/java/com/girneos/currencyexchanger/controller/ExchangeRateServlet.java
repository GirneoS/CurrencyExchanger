package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.dao.DAO;
import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.ExchangeRate;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeRateService;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.model.Currency;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService service;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            service = new ExchangeRateService();

            List<ExchangeRate> ratesList = service.getAll();

            if(!ratesList.isEmpty()) {

                Gson gson = new Gson();
                String textJson = gson.toJson(ratesList);

                resp.getWriter().write(textJson);
            }else{
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка")));
            }
        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new Gson().toJson(new Message("Ошибка на уровне БД")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");

        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");


        if(baseCode == null || targetCode == null || rate == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
        }

        try {
            service = new ExchangeRateService();


            Currency baseCurrency = currencyDAO.get(baseCode);
            Currency targetCurrency = currencyDAO.get(targetCode);

            if(baseCurrency==null || targetCurrency==null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(new Gson().toJson(new Message("Одна (или обе) валюта из валютной пары не существует в БД")));
            }else{

                if(exchangeRateDAO.get(baseCode+targetCode)!=null){
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write(new Gson().toJson(new Message("Валютная пара с таким кодом уже существует")));
                }else {

                    ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, Double.parseDouble(rate));

                    if (exchangeRateDAO.save(exchangeRate)) {
                        Gson gson = new Gson();
                        String json = gson.toJson(exchangeRate);

                        resp.setStatus(HttpServletResponse.SC_CREATED);
                        resp.getWriter().write(json);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        resp.getWriter().write(new Gson().toJson(new Message("Ошибка")));
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
