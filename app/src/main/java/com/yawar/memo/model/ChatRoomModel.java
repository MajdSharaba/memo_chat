package com.yawar.memo.model;

public class ChatRoomModel {
    public String name;
    public String userId;
    public String lastMessage;
    public String image;
    public boolean isChecked;
    public  String numberMessage;
    public String  chatId;
    public  String state;
    public  String numberUnRMessage;
    public  boolean inChat;
    public String fcmToken;
    public String lastMessageType;
    public String lastMessageState;
    public String specialNumber;
    public  String lastMessageTime;
    public boolean isTyping;







    public ChatRoomModel(String name,String senderId, String lastMessage, String image, boolean isChecked, String numberMessage,String chatId,String state,String numberUnRMessage,
                         boolean inChat,String fcmToken,String specialNumber, String lastMessageType, String lastMessageState,String lastMessageTime, boolean isTyping) {
        this.name = name;
        this.userId = senderId;
        this.lastMessage = lastMessage;
        this.image = image;
        this.isChecked = isChecked;
        this.numberMessage= numberMessage;
        this.chatId = chatId;
        this.state = state;
        this.numberUnRMessage = numberUnRMessage;
        this.inChat = inChat;
        this.fcmToken = fcmToken;
        this.specialNumber = specialNumber;
        this.lastMessageState= lastMessageState;
        this.lastMessageType = lastMessageType;
        this.lastMessageTime = lastMessageTime;
        this.isTyping = isTyping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
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
    public String getNumberUnRMessage() {
        return numberUnRMessage;
    }

    public void setNumberUnRMessage(String numberUnRMessage) {
        this.numberUnRMessage = numberUnRMessage;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public String getSpecialNumber() {
        return specialNumber;
    }

    public void setSpecialNumber(String specialNumber) {
        this.specialNumber = specialNumber;
    }

    public String getLastMessageType() {
        return lastMessageType;
    }

    public void setLastMessageType(String lastMessageType) {
        this.lastMessageType = lastMessageType;
    }

    public String getLastMessageState() {
        return lastMessageState;
    }

    public void setLastMessageState(String lastMessageState) {
        this.lastMessageState = lastMessageState;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

}
