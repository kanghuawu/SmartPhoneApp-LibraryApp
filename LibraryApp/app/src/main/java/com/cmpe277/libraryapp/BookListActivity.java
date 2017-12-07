package com.cmpe277.libraryapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import static com.cmpe277.libraryapp.DBHelper.getAllBookListFromDB;
import static com.cmpe277.libraryapp.DBHelper.getMyBookListFromDB;
import static com.cmpe277.libraryapp.LoginActivity.EMAIL;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

public class BookListActivity extends ListActivity {

    private DatabaseReference mDatabaseReference;
    private BookListAdaptor mAdapter;
    private ArrayList<Book> mBooks;
    private String mEmail;
    EditText editsearch;
    boolean isMyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences(LIB_PREFS, 0);

        mEmail = prefs.getString(EMAIL, "");
        editsearch = (EditText) findViewById(R.id.search);

        editsearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
                }
                return false;
            }
        });

        isMyList = getIntent().getBooleanExtra("myBookList", false);

        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editsearch.getText().toString().toLowerCase();
             //   Log.i("this is a test",text);
                mAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Book book = mBooks.get(position);

        Intent intentBookDetail = new Intent(BookListActivity.this, BookDetailActivity.class);
        intentBookDetail.putExtra("book", book);
        intentBookDetail.putExtra("isMyBookList", isMyList);
        startActivityForResult(intentBookDetail, 89);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBooks = new ArrayList<>();
        mAdapter = new BookListAdaptor(BookListActivity.this, mBooks);
        setListAdapter(mAdapter);
        Intent myBookListIntent = getIntent();
        boolean isMyBookList = myBookListIntent.getBooleanExtra("myBookList", false);
        if (isMyBookList) {
            getMyBookListFromDB(mDatabaseReference, mAdapter, mBooks, mEmail);
        } else {
            getAllBookListFromDB(mDatabaseReference, mAdapter, mBooks);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i("LibraryApp", "requestCode " + String.valueOf(requestCode));
//        Log.i("LibraryApp", "resultCode " + String.valueOf(resultCode));
        if (requestCode == 89 && resultCode == 45) {
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
