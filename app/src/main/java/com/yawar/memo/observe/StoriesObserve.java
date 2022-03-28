package com.yawar.memo.observe;


import com.yawar.memo.model.UserStatus;

import java.util.ArrayList;
import java.util.Observable;

public class StoriesObserve extends Observable  {
    ArrayList<UserStatus> userStatusList = new ArrayList<>();
    UserStatus myStatus;


    public UserStatus getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(UserStatus myStatus) {
        this.myStatus = myStatus;
        setChanged();
        notifyObservers();
    }



    public ArrayList<UserStatus> getUserStatusList() {
        return userStatusList;
    }

    public void setUserStatusList(ArrayList<UserStatus> userStatusList) {
        this.userStatusList = userStatusList;
        setChanged();
        notifyObservers();
    }
    public  void deleteStatus(int position){
        userStatusList.remove(position);
        setChanged();
        notifyObservers();
    }
    public  void addStatus(UserStatus userStatus){
        userStatusList.add( userStatus);
        setChanged();
        notifyObservers();
    }
    public  void  setAllSeen(int position){
        System.out.println(position+"position");
        userStatusList.get(position).setAllSeen(true);
        setChanged();
        notifyObservers();
    }
    public  void  setUserStatusSeen(int position,int StatusPosition){
        System.out.println(position+"position");
        userStatusList.get(position).getStatusList().get(StatusPosition).setSeen(true);
        setChanged();
        notifyObservers();
    }
    public UserStatus getUserStatus(int id) {
        return userStatusList.get(id) ;
    }

    public void deleteStory(String id){
        myStatus.deleteStory(id);
        setChanged();
        notifyObservers();
    }


}
