package com.example.classroom_location;

public class Msg {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    private String message;

    private int type;

    private String name;

    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Msg(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public Msg(String name, String message, int type, String time){
        this.name = name;
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type){
        this.type = type;
    }
}
