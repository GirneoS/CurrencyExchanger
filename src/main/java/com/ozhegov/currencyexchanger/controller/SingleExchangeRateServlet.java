package com.ozhegov.currencyexchanger.controller;

import com.google.gson.Gson;
import com.ozhegov.currencyexchanger.model.DataBaseHandler;
import com.ozhegov.currencyexchanger.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.crypto.Data;
import java.io.IOException;

@WebServlet("/api/exchangeRate/*")
public class SingleExchangeRateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        String strRate = pathInfo.substring(1);

        DataBaseHandler handler = new DataBaseHandler();
        ExchangeRate rate = handler.findExchangeRate(strRate);

        if(rate==null){
            System.out.println("a");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }else{
            System.out.println("b");
            resp.setStatus(HttpServletResponse.SC_OK);

            Gson gson = new Gson();
            String jsonResp = gson.toJson(rate);

            resp.getWriter().write(jsonResp);
        }
    }

}
