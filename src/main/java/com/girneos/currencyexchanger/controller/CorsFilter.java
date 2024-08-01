package com.girneos.currencyexchanger.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, PATCH");
        ((HttpServletResponse) response).addHeader("Access-Control-Max-Age", "86400");
        chain. doFilter(request, response);

    }
}
