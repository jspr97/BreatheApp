package com.example.breatheapp;

import java.util.Date;

public class Task {
    private String name;
    private String date;

    public Task() {}
    public Task(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
