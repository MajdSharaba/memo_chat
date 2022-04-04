package com.yawar.memo.observe;

import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.SendContactNumberResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class ContactNumberObserve extends Observable {
    ArrayList<SendContactNumberResponse> contactNumberResponseList  = new ArrayList<>();

    public ArrayList<SendContactNumberResponse> getContactNumberResponseList() {
        return contactNumberResponseList;
    }

    public void setContactNumberResponseList(ArrayList<SendContactNumberResponse> contactNumberResponseList) {
        this.contactNumberResponseList = contactNumberResponseList;
        setChanged();
        notifyObservers();
        System.out.println("is notiffyyyyyyyyyyyyyyy");
    }



}
