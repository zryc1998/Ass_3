package com.example.workouttimer;

public class TimerList {
    String name;
    String time;

    TimerList(String name, String time){
        this.name = name;
        this.time = time;
    }

    protected String getName(){
        return name;
    }

    protected String getTime(){
        return time;
    }
}
