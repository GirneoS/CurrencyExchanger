package com.girneos.currencyexchanger.controller;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.girneos.currencyexchanger.dao.CurrencyDAO;
import com.girneos.currencyexchanger.model.Message;
import com.girneos.currencyexchanger.service.CurrencyService;
import com.google.gson.Gson;
import com.girneos.currencyexchanger.dao.DAO;
import com.girneos.currencyexchanger.model.Currency;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService service;

    public void init() {
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            service = new CurrencyService();
            List<Currency> list = service.getAll();

            if (list.isEmpty()){

                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка")));
            }else {

                Gson gson = new Gson();
                String jsonResponse = gson.toJson(list);

                resp.getWriter().write(jsonResponse);
            }
        } catch (ClassNotFoundException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(new Gson().toJson(new Message("Ошибка на уровне БД")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json");

        String code = req.getParameter("code");
        String fullName = req.getParameter("name");
        String sign = req.getParameter("sign");

        if(code == null || fullName == null || sign == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            Message message = new Message("Отсутствует нужное поле формы");
            resp.getWriter().write(new Gson().toJson(message));
        }else {

            try {
                service = new CurrencyService();

                if(service.get(code)!=null){

                    resp.setStatus(HttpServletResponse.SC_CONFLICT);

                    Message message = new Message("Валюта с таким кодом уже существует");
                    resp.getWriter().write(new Gson().toJson(message));

                }else {
                    Currency currency = new Currency(code, fullName, sign);

                    if (service.save(currency)) {
                        resp.setStatus(HttpServletResponse.SC_CREATED);


                        resp.getWriter().write(new Gson().toJson(currency));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        resp.getWriter().write(new Gson().toJson(new Message("Ошибка во время сохранения")));
                    }

                }
            } catch (ClassNotFoundException | SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(new Gson().toJson(new Message("Ошибка на уровне БД")));
            }
        }


    }

    public void destroy() {
        super.destroy();
    }
}