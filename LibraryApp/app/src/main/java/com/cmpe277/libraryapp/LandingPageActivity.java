package com.cmpe277.libraryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cmpe277.libraryapp.strategies.LibrarianStrategy;
import com.cmpe277.libraryapp.strategies.PatronStrategy;
import com.cmpe277.libraryapp.strategies.UserStrategy;
import com.google.firebase.auth.FirebaseAuth;

import static com.cmpe277.libraryapp.LoginActivity.IS_LIBRARIAN;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

public class LandingPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);
        boolean isLibrarian = prefs.getBoolean(IS_LIBRARIAN, false);

        Log.i("LibraryApp", "IsLibrarian " + String.valueOf(isLibrarian));
        UserStrategy userStrategy;
        if (isLibrarian) {
            userStrategy = new LibrarianStrategy();

        } else {
            userStrategy = new PatronStrategy();
        }
        userStrategy.setUpLandingPage(LandingPageActivity.this);
    }

    public void logOut(View v) {
        Log.i("LibraryApp", "Loggin out");
        mAuth.signOut();
        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);
        prefs.edit().clear().apply();
        Intent loginIntent = new Intent(LandingPageActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
