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
import java.util.Arrays;

@WebServlet("/api/exchangeRate/*")
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

        DataBaseHandler handler = new DataBaseHandler();
        ExchangeRate rate = handler.findExchangeRate(strRate);

        if(rate==null){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }else{
            resp.setStatus(HttpServletResponse.SC_OK);

            Gson gson = new Gson();
            String jsonResp = gson.toJson(rate);

            resp.getWriter().write(jsonResp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        DataBaseHandler handler = new DataBaseHandler();
        ExchangeRate exchangeRate = handler.findExchangeRate(pathInfo.substring(1));

        String strRateParam = req.getParameter("rate");

        if (strRateParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Отсутствует нужное поле формы");
        } else {

            double newRate = Double.parseDouble(strRateParam);


            ExchangeRate updatedRate = handler.updateRate(exchangeRate, newRate);
            if (updatedRate != null) {
                resp.setStatus(HttpServletResponse.SC_OK);

                resp.setContentType("text/json");
                Gson gson = new Gson();

                String json = gson.toJson(updatedRate);
                resp.getWriter().write(json);
            }else{
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Валютная пара отсутствует в базе данных");
            }
        }
    }

}
