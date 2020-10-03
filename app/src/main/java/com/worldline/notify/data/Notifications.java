package com.worldline.notify.data;

public class Notifications {
    private int id;
    private String message;
    private String createdtime;

    public Notifications(int id, String message, String createdtime) {
        this.id = id;
        this.message = message;
        this.createdtime = createdtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }
}
