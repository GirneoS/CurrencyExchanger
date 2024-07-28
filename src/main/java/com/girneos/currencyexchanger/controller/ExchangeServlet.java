package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.controller.dao.DAO;
import com.girneos.currencyexchanger.controller.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.ExchangeRate;
import com.girneos.currencyexchanger.model.ExchangeOperation;
import com.girneos.currencyexchanger.model.Message;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if(from == null || to == null || amountStr == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
        }else{
            double amount = Double.parseDouble(amountStr);

            DAO<ExchangeRate> exchangeRateDAO = new ExchangeRateDAO();

            ExchangeRate exchangeRate = exchangeRateDAO.get(from+to);
            if(exchangeRate==null){
                ExchangeRate exchangeRateFrom = exchangeRateDAO.get(from+"USD");
                ExchangeRate exchangeRateTo = exchangeRateDAO.get("USD"+to);

                double convertedAmount = exchangeRateFrom.getRate()*amount*exchangeRateTo.getRate();
                ExchangeOperation exchangeOperation = new ExchangeOperation(exchangeRateFrom.getBaseCurrency(), exchangeRateTo.getTargetCurrency(), convertedAmount/amount, amount, convertedAmount);

                Gson gson = new Gson();
                String json = gson.toJson(exchangeOperation);

                resp.getWriter().write(json);

            }else{

                double convertedAmount = amount* exchangeRate.getRate();
                ExchangeOperation exchangeOperation = new ExchangeOperation(exchangeRate.getBaseCurrency(),exchangeRate.getTargetCurrency(),exchangeRate.getRate(),amount,convertedAmount);

                Gson gson = new Gson();
                String json = gson.toJson(exchangeOperation);

                resp.getWriter().write(json);
            }
        }
    }
}
