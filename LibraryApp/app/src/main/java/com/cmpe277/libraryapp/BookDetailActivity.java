package com.cmpe277.libraryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cmpe277.libraryapp.models.Book;
import com.cmpe277.libraryapp.strategies.LibrarianStrategy;
import com.cmpe277.libraryapp.strategies.PatronStrategy;
import com.cmpe277.libraryapp.strategies.UserStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.cmpe277.libraryapp.LoginActivity.IS_LIBRARIAN;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

public class BookDetailActivity extends AppCompatActivity {
    private Book mBook;
    private DatabaseReference mDatabaseReference;
    private boolean isMyBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_form);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);
        boolean isLibrarian = prefs.getBoolean(IS_LIBRARIAN, true);

        UserStrategy userStrategy;
        if (isLibrarian) {
            userStrategy = new LibrarianStrategy();

        } else {
            userStrategy = new PatronStrategy();
        }

        Intent bookListIntent = getIntent();
        mBook = (Book) bookListIntent.getSerializableExtra("book");
        isMyBookList =  (boolean) bookListIntent.getSerializableExtra("isMyBookList");

        if (mBook != null) {
            if(isMyBookList) {
                ((PatronStrategy)userStrategy).setUpDetailPageForMyList(BookDetailActivity.this, mDatabaseReference, mBook);
            } else {
                userStrategy.setUpDetailPage(BookDetailActivity.this, mDatabaseReference, mBook);
            }
        } else {
            Log.i("DEBUG", "mBook is null");
        }
    }
}
