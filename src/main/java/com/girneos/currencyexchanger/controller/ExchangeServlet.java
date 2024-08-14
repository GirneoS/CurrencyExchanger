package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.ExchangeOperation;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeOperationService;
import com.girneos.currencyexchanger.utils.Utils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeOperationService exchangeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, new Gson().toJson(new Message("Отсутствует нужное поле формы")));

        } else {
            try {
                BigDecimal amount = Utils.parseBigDecimal(amountStr);
                exchangeService = new ExchangeOperationService();

                ExchangeOperation operation = exchangeService.makeExchange(from, to, amount);

                Gson gson = new Gson();
                String json = gson.toJson(operation);

                resp.getWriter().write(json);

            } catch (ClassNotFoundException | SQLException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new Gson().toJson(new Message("Ошибка при получении данных из БД")));

            } catch (NoSuchExchangeRateException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, new Gson().toJson(new Message("Недостаточно информации для совершения обмена")));

            }
        }
    }
}
