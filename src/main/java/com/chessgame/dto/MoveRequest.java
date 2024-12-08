package com.chessgame.dto;


import jakarta.validation.constraints.NotEmpty;

public class MoveRequest {

    @NotEmpty(message = "'from' field is required.")
    private String from;

    @NotEmpty(message = "'to' field is required.")
    private String to;

    private String promotion;

    public MoveRequest() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}
