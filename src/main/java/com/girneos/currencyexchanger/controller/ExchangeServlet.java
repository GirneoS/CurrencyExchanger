package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.ExchangeOperation;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeOperationService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeOperationService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                service = new ExchangeOperationService();

                ExchangeOperation operation = service.makeExchange(from, to, amount);

                Gson gson = new Gson();
                String json = gson.toJson(operation);

                resp.getWriter().write(json);

            } catch (ClassNotFoundException | SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка при получении данных из БД")));
            } catch (NoSuchExchangeRateException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(new Gson().toJson(new Message("Недостаточно информации для совершения обмена")));
            }
        }
    }
}
