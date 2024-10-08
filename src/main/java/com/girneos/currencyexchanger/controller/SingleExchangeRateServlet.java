package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeRateService;
import com.girneos.currencyexchanger.utils.Utils;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class SingleExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Content-Type","application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        String strRate = pathInfo.substring(1);

        if (strRate.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    new Gson().toJson(new Message("Коды валют пары отсутствуют в адресе")));

        } else {
            try {
                exchangeRateService = new ExchangeRateService();
                ExchangeRate exchangeRate = exchangeRateService.get(strRate.substring(0,3),strRate.substring(3));

                String jsonResp = new Gson().toJson(exchangeRate);
                resp.getWriter().write(jsonResp);

            } catch (ClassNotFoundException | SQLException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new Gson().toJson(new Message("Ошибка на при получении данных БД")));

            } catch (NoSuchExchangeRateException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        new Gson().toJson(new Message("Обменный курс для пары не найден")));

            }
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader("Content-Type","application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        try {
            String strRateParam;

            if(req.getReader()==null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        new Gson().toJson(new Message("Отсутствует нужное поле формы")));
                return;
            }

            try (BufferedReader reader = req.getReader()) {
                strRateParam = reader.readLine().split("=")[1];
            }

            if (strRateParam == null || strRateParam.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        new Gson().toJson(new Message("Отсутствует нужное поле формы")));

            } else {
                exchangeRateService = new ExchangeRateService();
                BigDecimal newRate = Utils.parseBigDecimal(strRateParam);

                ExchangeRate updatedRate = exchangeRateService.update(code.substring(0,3), code.substring(3), newRate);

                String json = new Gson().toJson(updatedRate);
                resp.getWriter().write(json);
            }
        } catch (ClassNotFoundException | SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new Gson().toJson(new Message("Ошибка на уровне БД")));

        } catch (NoSuchExchangeRateException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    new Gson().toJson(new Message("Валютная пара отсутствует в базе данных")));
        }
    }

}
