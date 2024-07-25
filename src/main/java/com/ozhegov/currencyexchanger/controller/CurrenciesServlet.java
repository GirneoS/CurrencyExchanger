package com.ozhegov.currencyexchanger.controller;

import java.io.*;
import java.lang.ref.PhantomReference;
import java.util.List;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import com.ozhegov.currencyexchanger.model.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/api/currencies")
public class CurrenciesServlet extends HttpServlet {

    public void init() {
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        DataBaseHandler handler = new DataBaseHandler();
        List<Currency> list = handler.findAllCurrencies();

        if (list.isEmpty()){

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }else {

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(list);

            resp.getWriter().write(jsonResponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String fullName = req.getParameter("name");
        String sign = req.getParameter("sign");

        if(code.isEmpty() || fullName.isEmpty() || sign.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Отсутствует нужное поле формы");
        }else {
            DataBaseHandler handler = new DataBaseHandler();

            if(handler.findCurrencyByCode(code)!=null){

                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("Валюта с таким кодом уже существует");

            }else {

                Currency currency = new Currency(code, fullName, sign);

                if (handler.insertCurrency(currency)) {
                    resp.setContentType("text/json");
                    resp.setStatus(HttpServletResponse.SC_CREATED);

                    Gson gson = new Gson();
                    String json = gson.toJson(currency);

                    resp.getWriter().write(json);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("Ошибка");
                }

            }
        }
    }

    public void destroy() {
    }
}