package com.yawar.memo.model;

import android.text.Spannable;
import android.text.SpannableString;

import java.util.Objects;

public class ChatMessage implements Comparable, Cloneable  {
    public boolean isMe() {
        return isMe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String id;
    private boolean isMe;
//    public  Spannable message;
    public String message;
    private String image;
    private  String type;
    private String userId;
    private String dateTime;
    private  String state;
    private  String fileName;
    private boolean isDownload = false;
    private boolean upload = false;




    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }

    private  String isUpdate;



    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked;


    public ChatMessage(String id, boolean isMe, String message, String userId, String dateTime,String type,String state,String fileName,boolean isDownload ) {
        this.id = id;
        this.isMe = isMe;
        //this.message = new SpannableString(message);
        this.message=message;
        this.userId = userId;
        this.dateTime = dateTime;
        this.type = type;
        this.state = state;
        this.fileName = fileName;
        this.isDownload  = isDownload;
    }

    public ChatMessage() {

    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
//    public Spannable getMessage() {
//        return message;
//    }
    public  String getMessage(){
        return message;
    }
    public void setMessage(String message) {

        //this.message=new SpannableString(message);
        this.message=message;

    }
//    public void setMessage(Spannable message) {
//
//        this.message=message;
//
//    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(Object o) {
        ChatMessage compare = (ChatMessage) o;
        if (
//                compare.getMessage().equals(this.getMessage()) &&
                Objects.equals(compare.state, this.state) &&
                compare.isDownload == (this.isDownload)&&
                        compare.isUpload() == (this.isUpload())&&

                        compare.isChecked == (this.isChecked)&&
                        Objects.equals(compare.isUpdate, this.isUpdate)

        ) {
            System.out.println(compare.getMessage()+this.getMessage()+"compareTo"+1);

            return 0;
        }
        System.out.println(compare.getMessage()+this.getMessage()+"Not compareTo"+1);

        return 1;

    }
    @Override
    public ChatMessage clone() {

        ChatMessage clone;
        try {
            clone = (ChatMessage) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }
}
