package com.yawar.memo.model;

import java.util.ArrayList;

public class Status {
    private boolean isSeen;
    private  String url;
    private String type;
    private String id;
    private String user_id;
    private ArrayList<UserSeen> userSeens;

    

    public Status(boolean isSeen, String url,String type ) {
        this.isSeen = isSeen;
        this.url =url;
        this.type=type;
    }

    public Status(String id , String user_id,boolean isSeen, String url, String type, ArrayList<UserSeen> userSeens) {
        this.id=id;
        this.user_id=user_id;
        this.isSeen = isSeen;
        this.url = url;
        this.type = type;
        this.userSeens = userSeens;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        System.out.println("public void setSeen");
        isSeen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<UserSeen> getUserSeens() {
        return userSeens;
    }

    public void setUserSeens(ArrayList<UserSeen> userSeens) {
        this.userSeens = userSeens;
    }
    public void addUserSeens(UserSeen userSeen) {
        this.userSeens.add(userSeen);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
