package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.model.exception.NoSuchCurrencyException;
import com.girneos.currencyexchanger.service.CurrencyService;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.model.Currency;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@WebServlet("/currency/*")
public class SingleCurrencyServlet extends HttpServlet {
    private CurrencyService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (req.getPathInfo().isEmpty() || req.getPathInfo().equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(new Gson().toJson(new Message("Код валюты отсутствует в адресе")));
        } else {

            String pathInfo = req.getPathInfo();
            String code = pathInfo.substring(1);

            try {
                service = new CurrencyService();

                Currency currency = service.get(code);

                Gson gson = new Gson();
                String json = gson.toJson(currency);

                resp.getWriter().write(json);
            } catch (ClassNotFoundException | SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка на уровне БД")));
            } catch (NoSuchCurrencyException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(new Gson().toJson(new Message("Валюта не найдена")));
            }
        }
    }
}
