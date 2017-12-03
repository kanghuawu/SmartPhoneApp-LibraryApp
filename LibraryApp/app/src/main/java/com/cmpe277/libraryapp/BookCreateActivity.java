package com.cmpe277.libraryapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cmpe277.libraryapp.strategies.LibrarianStrategy;
import com.cmpe277.libraryapp.strategies.PatronStrategy;
import com.cmpe277.libraryapp.strategies.UserStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.cmpe277.libraryapp.LoginActivity.IS_LIBRARIAN;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

public class BookCreateActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_form);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);
        boolean isLibrarian = prefs.getBoolean(IS_LIBRARIAN, true);

        LibrarianStrategy librarianStrategy;
        if (isLibrarian) {
            librarianStrategy = new LibrarianStrategy();
            librarianStrategy.setUpBookCreatingPage(BookCreateActivity.this, mDatabaseReference);
        }
    }
}
