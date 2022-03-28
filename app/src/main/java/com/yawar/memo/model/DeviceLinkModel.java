package com.yawar.memo.model;

public class DeviceLinkModel {
    String name;
    String image;
    String time;

    public DeviceLinkModel(String name,String image,String time) {
        this.name = name;
        this.image = image;
        this.time= time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
