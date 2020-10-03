package com.worldline.notify.data;

public class Device {
    private int userid;
    private String name;
    private String token;


    public Device(int userid, String name, String token){
        this.userid =userid;
        this.name = name;
        this.token = token;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
