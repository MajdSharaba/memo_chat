package com.yawar.memo.model;

import java.util.Objects;

public class SearchRespone  implements Comparable, Cloneable {
    String id;
    String name;
    String SecretNumber;
    String image;
    String phone;
    String token;



    boolean isAdded;

    public String getBlockedFor() {
        return blockedFor;
    }

    public void setBlockedFor(String blockedFor) {
        this.blockedFor = blockedFor;
    }

    String blockedFor;

    public SearchRespone(String id, String name, String secretNumber, String image,String phone,String token,String blockedFor,boolean isAdded) {
        this.id = id;
        this.name = name;
        SecretNumber = secretNumber;
        this.image = image;
        this.phone= phone;
        this.token=token;
        this.blockedFor = blockedFor;
        this.isAdded = isAdded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecretNumber() {
        return SecretNumber;
    }

    public void setSecretNumber(String secretNumber) {
        SecretNumber = secretNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public int compareTo(Object o) {
        SearchRespone compare = (SearchRespone) o;
        if (compare.id.equals(this.id))  {
            System.out.println("return 0");
            return 0;
        }
        System.out.println("return 1");

        return 1;
    }
    @Override
    public SearchRespone clone() {

        SearchRespone clone;
        try {
            clone = (SearchRespone) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }
}
