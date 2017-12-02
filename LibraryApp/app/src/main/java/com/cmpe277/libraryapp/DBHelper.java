package com.cmpe277.libraryapp;

import com.cmpe277.libraryapp.models.User;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by bondk on 12/2/17.
 */

public class DBHelper {
    public static final String USER_DB = "user";

    public static void saveUserInfoToDB(DatabaseReference databaseReference, User user) {
        databaseReference.child(USER_DB).child(user.getEmail()).setValue(user);
    }
}
