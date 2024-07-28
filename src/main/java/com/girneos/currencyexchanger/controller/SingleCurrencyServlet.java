package com.ozhegov.currencyexchanger.controller;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.Currency;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet("/api/currency/*")
public class SingleCurrencyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if(req.getPathInfo().isEmpty() || req.getPathInfo().equals("/")){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Код валюты отсутствует в адресе");
        }else {

            String pathInfo = req.getPathInfo();
            String currencyCode = pathInfo.substring(1);

            DataBaseHandler handler = new DataBaseHandler();
            List<Currency> list = handler.findAllCurrencies();

            Currency currency = list.stream()
                    .filter(c -> Objects.equals(c.getCode(), currencyCode))
                    .findFirst().orElse(null);

            if (currency==null){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Валюта не найдена");
            }else {

                Gson gson = new Gson();
                String json = gson.toJson(currency);

                resp.getWriter().write(json);
            }
        }
    }
}
