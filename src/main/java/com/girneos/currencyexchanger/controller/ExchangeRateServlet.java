package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;
import com.girneos.currencyexchanger.model.ExchangeRate;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeRateService;
import com.girneos.currencyexchanger.utils.Utils;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jshell.execution.Util;

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

            Gson gson = new Gson();
            String textJson = gson.toJson(ratesList);

            resp.getWriter().write(textJson);
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


        if (baseCode == null || targetCode == null || rate == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
        } else {

            try {
                service = new ExchangeRateService();
                ExchangeRate exchangeRate = service.save(baseCode, targetCode, Utils.parseBigDecimal(rate));

                Gson gson = new Gson();
                String json = gson.toJson(exchangeRate);

                resp.getWriter().write(json);

            } catch (ClassNotFoundException | SQLException e) {
                if (e.getMessage().startsWith("[SQLITE_CONSTRAINT_UNIQUE]")) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write(new Gson().toJson(new Message("Валютная пара с таким кодом уже существует")));
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(new Gson().toJson(new Message("Ошибка при получении данных из БД")));
                }
            } catch (NoSuchCurrencyException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(new Gson().toJson(new Message("Одна (или обе) валюта из валютной пары не существует в БД")));
            }
        }
    }

}
