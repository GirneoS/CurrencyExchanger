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
        resp.addHeader("Content-Type","application/json;charset=UTF-8");

        try {
            service = new ExchangeRateService();

            List<ExchangeRate> ratesList = service.getAll();

            Gson gson = new Gson();
            String textJson = gson.toJson(ratesList);

            resp.getWriter().write(textJson);
        } catch (ClassNotFoundException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new Gson().toJson(new Message("Ошибка на уровне БД")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-Type","application/json;charset=UTF-8");

        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");


        if (!Utils.isValidExchangeRateArgs(baseCode, targetCode, rate)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    new Gson().toJson(new Message("Отсутствует нужное поле формы")));
            return;
        }

        try {
            service = new ExchangeRateService();
            ExchangeRate exchangeRate = service.save(baseCode, targetCode, Utils.parseBigDecimal(rate));

            Gson gson = new Gson();
            String json = gson.toJson(exchangeRate);

            resp.getWriter().write(json);

        } catch (ClassNotFoundException | SQLException e) {
            if (e.getMessage().startsWith("[SQLITE_CONSTRAINT_UNIQUE]"))
                resp.sendError(HttpServletResponse.SC_CONFLICT,
                        new Gson().toJson(new Message("Валютная пара с таким кодом уже существует")));
            else
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new Gson().toJson(new Message("Ошибка при получении данных из БД")));

        } catch (NoSuchCurrencyException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    new Gson().toJson(new Message("Одна (или обе) валюта из валютной пары не существует в БД")));
        }
    }

}
