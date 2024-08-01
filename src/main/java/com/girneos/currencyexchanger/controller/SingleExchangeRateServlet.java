package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.dao.DAO;
import com.girneos.currencyexchanger.dao.ExchangeRateDAO;
import com.girneos.currencyexchanger.model.Message;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class SingleExchangeRateServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getMethod().equals("PATCH")){
            doPatch(req, resp);
        }else{
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        String strRate = pathInfo.substring(1);

        DAO<ExchangeRate> exchangeRateDAO = new ExchangeRateDAO();
        ExchangeRate exchangeRate = exchangeRateDAO.get(strRate);

        if(exchangeRate==null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

            resp.getWriter().write(new Gson().toJson(new Message("Валютная пара с таким кодом не найдена")));
        }else{
            resp.setStatus(HttpServletResponse.SC_OK);

            String jsonResp = new Gson().toJson(exchangeRate);

            resp.getWriter().write(jsonResp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");

        String pathInfo = req.getPathInfo();

        DAO<ExchangeRate> exchangeRateDAO = new ExchangeRateDAO();
        ExchangeRate exchangeRate = exchangeRateDAO.get(pathInfo.substring(1));



        String strRateParam;

        try(BufferedReader reader = req.getReader()) {
            strRateParam = reader.readLine().split("=")[1];
        }

        if (strRateParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
        } else {

            double newRate = Double.parseDouble(strRateParam);


            ExchangeRate updatedRate = exchangeRateDAO.update(exchangeRate, newRate);
            if (updatedRate != null) {
                resp.setStatus(HttpServletResponse.SC_OK);

                String json = new Gson().toJson(updatedRate);
                resp.getWriter().write(json);
            }else{
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

                resp.getWriter().write(new Gson().toJson(new Message("Валютная пара отсутствует в базе данных")));
            }
        }
    }

}
