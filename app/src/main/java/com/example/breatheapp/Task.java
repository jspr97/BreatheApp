package com.example.breatheapp;

import java.util.ArrayList;

public class Task {
    private String name, date, time;
    private boolean done = false;
    private ArrayList<String> users;

    public  Task() {}

    public Task(String name, String date, String time, boolean done, ArrayList<String> users) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.done = done;
        this.users = users;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public boolean getDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public ArrayList<String> getUsers() { return users; }
    public void setUsers(ArrayList<String> users) { this.users = users; }
}
