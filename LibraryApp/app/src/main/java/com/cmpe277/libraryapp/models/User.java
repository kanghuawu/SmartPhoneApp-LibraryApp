package com.cmpe277.libraryapp.models;

import java.util.List;

/**
 * Created by khwu on 12/19/17.
 */

public class User {
    private String email;
    private List<String> waitList;
    private List<String> myList;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getWaitList() {
        return waitList;
    }

    public void setWaitList(List<String> waitList) {
        this.waitList = waitList;
    }

    public List<String> getMyList() {
        return myList;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }
}
