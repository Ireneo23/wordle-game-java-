package com.example.wordleapp.model;

public class GameHistoryModel {
    private String status; // Win / Lose
    private String dateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public GameHistoryModel(String status, String dateTime) {
        this.status = status;
        this.dateTime = dateTime;
    }

}
