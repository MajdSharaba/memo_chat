package com.yawar.memo.model;

import java.util.ArrayList;
import java.util.List;

public class UserStatus {
    private String userName;
    private boolean allSeen;
    private ArrayList<Status> statusList;


    public UserStatus(String userName, boolean areAllSeen, ArrayList<Status> statusList) {
        this.userName = userName;
        this.allSeen = areAllSeen;
        this.statusList = statusList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean areAllSeen() {
        return allSeen;
    }

    public void setAllSeen(boolean allSeen) {
        System.out.println("setAllSeen");
        this.allSeen = allSeen;
    }
    public  void  deleteStory(String id){
        for(Status status:statusList){
            if(status.getId().equals(id)){
                statusList.remove(status);
                break;
            }
        }

    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(ArrayList<Status> statusList) {
        this.statusList = statusList;
    }
}
