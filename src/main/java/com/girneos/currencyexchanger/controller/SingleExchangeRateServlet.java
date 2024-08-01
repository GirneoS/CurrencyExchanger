package com.girneos.currencyexchanger.controller;

import com.girneos.currencyexchanger.model.exception.NoSuchExchangeRateException;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.ExchangeRateService;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class SingleExchangeRateServlet extends HttpServlet {
    private ExchangeRateService service;

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
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        String strRate = pathInfo.substring(1);

        if (strRate.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(new Gson().toJson(new Message("Коды валют пары отсутствуют в адресе")));
        } else {
            try {
                service = new ExchangeRateService();
                ExchangeRate exchangeRate = service.get(strRate);

                resp.setStatus(HttpServletResponse.SC_OK);
                String jsonResp = new Gson().toJson(exchangeRate);
                resp.getWriter().write(jsonResp);

            } catch (ClassNotFoundException | SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка на при получении данных БД")));
            } catch (NoSuchExchangeRateException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(new Gson().toJson(new Message("Обменный курс для пары не найден")));
            }
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");

        String pathInfo = req.getPathInfo();

        try {
            String strRateParam;

            try (BufferedReader reader = req.getReader()) {
                strRateParam = reader.readLine().split("=")[1];
            }

            if (strRateParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                resp.getWriter().write(new Gson().toJson(new Message("Отсутствует нужное поле формы")));
            } else {

                service = new ExchangeRateService();
                double newRate = Double.parseDouble(strRateParam);

                ExchangeRate updatedRate = service.update(pathInfo.substring(1), newRate);

                resp.setStatus(HttpServletResponse.SC_OK);

                String json = new Gson().toJson(updatedRate);
                resp.getWriter().write(json);
            }
        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new Gson().toJson(new Message("Ошибка на уровне БД")));
        } catch (NoSuchExchangeRateException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(new Gson().toJson(new Message("Валютная пара отсутствует в базе данных")));
        }
    }

}
