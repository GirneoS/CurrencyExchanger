package com.girneos.currencyexchanger.model;

public class Message {
    public Message(String message) {
        this.message = message;
    }

    private String message = "Ошибка";

    public String getMessage() {
        return message;
    }
}
