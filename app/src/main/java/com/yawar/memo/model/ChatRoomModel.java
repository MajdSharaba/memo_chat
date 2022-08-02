package com.yawar.memo.model;

import java.util.Objects;

public class ChatRoomModel implements Comparable, Cloneable {
//    public String username;
//    public String other_id;
//    public String lastMessage;
//    public String image;
//    public boolean isChecked;
//    public  String numberMessage;
//    public String  chatId;
//    public  String state;
//    public  String numberUnRMessage;
//    public  boolean inChat;
//    public String fcmToken;
//    public String lastMessageType;
//    public String lastMessageState;
//    public String specialNumber;
//    public  String lastMessageTime;
//    public boolean isTyping;
//    public  String blockedFor;
    ////
    public String username;
    public String other_id;
    public String last_message;
    public String image;
    public boolean isChecked;
    public  String numberMessage;
    public String id;
    public  String state;
    public  String num_msg;
    public  boolean inChat;
    public String user_token;
    public String message_type;
    public String mstate;
    public String sn;
    public  String created_at;
    public boolean isTyping;
    public  String blocked_for;


//           "deleted":null,
//            "blocked":false,"blocked_for":null,"num_msg":0,"last_message":"\u0628\u0628\u0628",
//            ","mstate":"1","msg_sender":"198",
//            "msg_reciver":"199","created_at":"1659009603214","archive":false}


    public ChatRoomModel(String name,String senderId, String lastMessage, String image, boolean isChecked, String numberMessage,String chatId,String state,String numberUnRMessage,
                         boolean inChat,String fcmToken,String specialNumber, String lastMessageType, String lastMessageState,String lastMessageTime, boolean isTyping, String blockedFor) {
        this.username = name;
        this.other_id = senderId;
        this.last_message = lastMessage;
        this.image = image;
        this.isChecked = isChecked;
        this.numberMessage= numberMessage;
        this.id = chatId;
        this.state = state;
        this.num_msg = numberUnRMessage;
        this.inChat = inChat;
        this.user_token = fcmToken;
        this.sn = specialNumber;
        this.mstate = lastMessageState;
        this.message_type = lastMessageType;
        this.created_at = lastMessageTime;
        this.isTyping = isTyping;
        this.blocked_for = blockedFor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOther_id() {
        return other_id;
    }

    public void setOther_id(String other_id) {
        this.other_id = other_id;
    }


    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getNumberMessage() {
        return numberMessage;
    }

    public void setNumberMessage(String numberMessage) {
        this.numberMessage = numberMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public boolean isInChat() {
        return inChat;
    }

    public void setInChat(boolean inChat) {
        this.inChat = inChat;
    }
    public String getNum_msg() {
        return num_msg;
    }

    public void setNum_msg(String num_msg) {
        this.num_msg = num_msg;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }
    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getMstate() {
        return mstate;
    }

    public void setMstate(String mstate) {
        this.mstate = mstate;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }


    public String getBlocked_for() {
        return blocked_for;
    }

    public void setBlocked_for(String blocked_for) {
        this.blocked_for = blocked_for;
    }

    @Override
    public int compareTo(Object o) {
        ChatRoomModel compare = (ChatRoomModel) o;
        if (compare.getOther_id().equals(this.getOther_id()) && compare.last_message.equals(this.last_message) && compare.isTyping == (this.isTyping)&& Objects.equals(compare.getBlocked_for(),this.getBlocked_for())
        ) {
            return 0;
        }
        return 1;
    }

    @Override
    public ChatRoomModel clone() {

        ChatRoomModel clone;
        try {
            clone = (ChatRoomModel) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }
}
