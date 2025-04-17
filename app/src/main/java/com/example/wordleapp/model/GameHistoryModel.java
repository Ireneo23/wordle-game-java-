package com.example.wordleapp.model;

public class GameHistoryModel {
    private String status; // Win / Lose
    private int attempts;
    private String dateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public GameHistoryModel(String status, int attempts, String dateTime) {
        this.status = status;
        this.attempts = attempts;
        this.dateTime = dateTime;
    }


    // Getters...
}
