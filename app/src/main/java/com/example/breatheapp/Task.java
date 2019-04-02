package com.example.breatheapp;

public class Task {
    private String name, date, time, user;
    private boolean done = false;

    public  Task() {}

    public Task(String name, String date, String time, String user, boolean done) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.user = user;
        this.done = done;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public boolean getDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
