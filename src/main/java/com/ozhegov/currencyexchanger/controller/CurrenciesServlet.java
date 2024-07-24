package com.ozhegov.currencyexchanger.controller;

import java.io.*;
import java.util.List;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import com.ozhegov.currencyexchanger.model.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/api/currency")
public class CurrenciesServlet extends HttpServlet {

    public void init() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        DataBaseHandler handler = new DataBaseHandler();
        List<Currency> list = handler.findAllCurrencies();

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(list);

        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }

    public void destroy() {
    }
}