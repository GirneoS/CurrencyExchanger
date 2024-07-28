package com.ozhegov.currencyexchanger.controller;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import com.ozhegov.currencyexchanger.model.dto.ExchangeOperationDTO;
import com.ozhegov.currencyexchanger.model.ExchangeRate;
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
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if(from == null || to == null || amountStr == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            String message = "Отсутствует нужное поле формы";
            Gson gson = new Gson();
            String json = gson.toJson(message);

            resp.getWriter().write(json);
        }else{
            double amount = Double.parseDouble(amountStr);

            DataBaseHandler handler = new DataBaseHandler();

            ExchangeRate exchangeRate = handler.findExchangeRate(from+to);
            if(exchangeRate==null){
                /*
                Если такой пары не существует в БД, то делаем обмен через валютные пары с USD.
                 */
                ExchangeRate exchangeRateFrom = handler.findExchangeRate(from+"USD");
                ExchangeRate exchangeRateTo = handler.findExchangeRate("USD"+to);

                double convertedAmount = exchangeRateFrom.getRate()*amount*exchangeRateTo.getRate();
                ExchangeOperationDTO exchangeOperationDTO = new ExchangeOperationDTO(exchangeRateFrom.getBaseCurrency(), exchangeRateTo.getTargetCurrency(), convertedAmount/amount, amount, convertedAmount);

                Gson gson = new Gson();
                String json = gson.toJson(exchangeOperationDTO);

                resp.setContentType("text/json");
                resp.getWriter().write(json);

            }else{

                double convertedAmount = amount* exchangeRate.getRate();
                ExchangeOperationDTO exchangeOperationDTO = new ExchangeOperationDTO(exchangeRate.getBaseCurrency(),exchangeRate.getTargetCurrency(),exchangeRate.getRate(),amount,convertedAmount);

                Gson gson = new Gson();
                String json = gson.toJson(exchangeOperationDTO);

                resp.setContentType("text/json");
                resp.getWriter().write(json);
            }
        }
    }
}
