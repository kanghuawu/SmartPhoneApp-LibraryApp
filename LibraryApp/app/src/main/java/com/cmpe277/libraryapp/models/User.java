package com.cmpe277.libraryapp.models;

/**
 * Created by bondk on 12/1/17.
 */

public class User {
    private String email;
    private String userName;

    public User(String email, String userName) {
        this.email = email;
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }
}
