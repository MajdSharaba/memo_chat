package com.yawar.memo.model;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomRespone {
    private ArrayList<ChatRoomModel> data;

    public ChatRoomRespone(ArrayList<ChatRoomModel> items) {
        this.data = items;
    }

    public ArrayList<ChatRoomModel> getData() {
        return data;
    }

    public void setData(ArrayList<ChatRoomModel> data) {
        this.data = data;
    }
}
