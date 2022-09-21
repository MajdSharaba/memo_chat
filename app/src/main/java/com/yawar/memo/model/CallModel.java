package com.yawar.memo.model;

import java.util.Date;

public class CallModel implements Comparable, Cloneable {

     String id;
     String username;
     String caller_id;
     String image;
     String call_type;
     String answer_id;
     String call_status;
     String duration;
     String createdAt;

    public CallModel( String id,String username, String caller_id, String image,String call_type,String answer_id,  String call_status,String duration, String createdAt) {
        this.username = username;
        this.caller_id = caller_id;
        this.image = image;
        this.id = id;
        this.call_type = call_type;
        this.answer_id = answer_id;
        this.call_status = call_status;
        this.duration = duration;
        this.createdAt = createdAt;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaller_id() {
        return caller_id;
    }

    public void setCaller_id(String caller_id) {
        this.caller_id = caller_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getCall_status() {
        return call_status;
    }

    public void setCall_status(String call_status) {
        this.call_status = call_status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public int compareTo(Object o) {
        CallModel compare = (CallModel) o;
        if (compare.getId().equals(this.getId())) {
            return 0;
        }
        return 1;
    }

    @Override
    public CallModel clone() {

        CallModel clone;
        try {
            clone = (CallModel) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }


}