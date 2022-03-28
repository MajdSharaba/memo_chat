package com.yawar.memo.observe;

import com.yawar.memo.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ChatRoomObserve extends Observable  {
    List<ChatRoomModel> chatRoomModelList  = new ArrayList<>();
    boolean isArchived= false;

    public List<ChatRoomModel> getChatRoomModelList() {
        return chatRoomModelList;
    }

    public void setChatRoomModelList(List<ChatRoomModel> chatRoomModelList) {
        this.chatRoomModelList = chatRoomModelList;
        setChanged();
        notifyObservers();
    }
    public  void deleteChatRoom(String chatId){
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoomModelList.remove(chatRoom);
                break;
            }}
        setChanged();
        notifyObservers();
    }
    public  void addChatRoom(ChatRoomModel chatRoomModel){
//        System.out.println("addddddddddddddddddddddddddddddddddd");
        chatRoomModelList.add( 0,chatRoomModel);
        setChanged();
        notifyObservers();
    }
    public void setLastMessage(String message , String chatId,String senderId, String reciverId,String type,String state,String dateTime){
        boolean inList=false;
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoom.setLastMessage(message);
                chatRoom.setLastMessageType(type);
                chatRoom.setLastMessageState(state);
                chatRoom.setLastMessageTime(dateTime);





                inList=true;

                if(!chatRoom.inChat){
                    chatRoom.setNumberUnRMessage(String.valueOf(Integer.parseInt(chatRoom.getNumberUnRMessage())+1));
                }

                chatRoomModelList.remove(chatRoom);
                chatRoomModelList.add(0,chatRoom);
                break;
            }
        }

        if(!inList){
            for(ChatRoomModel chatRoom:chatRoomModelList){
                if(chatRoom.chatId.equals(senderId+reciverId)){
                    chatRoomModelList.remove(chatRoom);
                    System.out.println("chatRoom.setLastMessage(message)outtttttttttttttt chatrommmmmmmmmmm");
                    if(!chatRoom.inChat){
                        chatRoom.setNumberUnRMessage(String.valueOf(Integer.parseInt(chatRoom.getNumberUnRMessage())+1));
                    }
                    chatRoom.setChatId(chatId);
                    System.out.println("removeeeeeeeeeeeee and Addddddddd");
                    chatRoomModelList.remove(chatRoom);
                    chatRoomModelList.add(0,chatRoom);

                    break;
                }

                }
            }

        setChanged();
        notifyObservers();
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
        setChanged();
        notifyObservers();
    }
/// state 1 for Archived ChatRoom
    public void setState(String chatId,String state) {
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.chatId.equals(chatId)){
                chatRoom.setState(state);

                break;
            }
        }
        setChanged();
        notifyObservers();
    }

    public void setInChat(String chat_id,boolean state){
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.chatId.equals(chat_id)){
                chatRoom.setInChat(state);
                if(state){
                chatRoom.setNumberUnRMessage("0");}

                break;
            }
            setChanged();
            notifyObservers();

        }
    }
    public String getChatId(String anthorUserId){
        System.out.println("getChatId"+anthorUserId);
        for(ChatRoomModel chatRoom:chatRoomModelList){
            if(chatRoom.userId.equals(anthorUserId)){
                System.out.println("chsat_id issssssssssssssssssssssssssss"+chatRoom.chatId);

                return chatRoom.chatId;
            }


        }
        System.out.println("chsat_id issssssssssssssssssssssssssss"+"nulll");
        return "";

    }
    public  boolean checkInChat(String chat_id) {
        for (ChatRoomModel chatRoom : chatRoomModelList) {
            if (chatRoom.chatId.equals(chat_id)) {
                if (chatRoom.inChat) {
                    return true;
                } else {
                    return false;
                }

            }
        }

  return  false;
    }
    public  void setTyping(String chat_id,boolean isTyping) {
        for (ChatRoomModel chatRoom : chatRoomModelList) {
            if (chatRoom.chatId.equals(chat_id)) {
              chatRoom.setTyping(isTyping);
              break;

            }
        }
        setChanged();
        notifyObservers();
    }
}
