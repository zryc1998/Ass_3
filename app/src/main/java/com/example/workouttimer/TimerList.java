package com.example.workouttimer;

public class TimerList {
    String name;
    long time;

    TimerList(String name, long time){
        this.name = name;
        this.time = time;
    }

    protected String getName(){
        return name;
    }

    protected long getTime(){
        return time;
    }
}
