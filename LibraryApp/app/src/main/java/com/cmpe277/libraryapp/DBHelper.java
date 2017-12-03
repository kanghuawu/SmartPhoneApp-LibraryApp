package com.cmpe277.libraryapp;

import android.util.Log;
import android.widget.BaseAdapter;

import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by bondk on 12/2/17.
 */

public class DBHelper {
    public static final String USER_DB = "user";
    public static final String BOOK_DB = "book";

    public static final String BOOK_STATUS = "status";
    public static final String BOOK_STATUS_RENT = "Rent";
    public static final String BOOK_STATUS_AVAILABLE = "Available";


    public static void getAllBookListFromDB(DatabaseReference databaseReference, final BaseAdapter adapter, final ArrayList<Book> books) {
        databaseReference.child(BOOK_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    books.add(child.getValue(Book.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getMyBookListFromDB(DatabaseReference databaseReference, final BaseAdapter adapter, final ArrayList<Book> books, String email) {
        databaseReference.child(USER_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    books.add(child.getValue(Book.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addOrUpdateNewBookToDB(DatabaseReference databaseReference, Book book) {
        Log.i("LibraryApp", "calling addOrUpdateNewBookToDB " + book);
        databaseReference.child(BOOK_DB).child(book.getCallNumber()).setValue(book);
    }

    public static void removeBookFromDB(DatabaseReference databaseReference, String callNum) {
        databaseReference.child(BOOK_DB).child(callNum).removeValue();
    }

    public static void rentBook(DatabaseReference databaseReference, Book book, String email) {
        email = email.replace(".", "dot");
        databaseReference.child(USER_DB).child(email)
                .child(book.getCallNumber())
                .setValue(book);
        databaseReference.child(BOOK_DB)
                .child(book.getCallNumber())
                .child(BOOK_STATUS)
                .setValue(BOOK_STATUS_RENT);
    }

    public static void returnBook(DatabaseReference databaseReference, Book book, String email) {
        email = email.replace(".", "dot");
        databaseReference.child(USER_DB).child(email)
                .child(book.getCallNumber())
                .removeValue();
        databaseReference.child(BOOK_DB).child(email)
                .child(book.getCallNumber())
                .child(BOOK_STATUS)
                .setValue(BOOK_STATUS_AVAILABLE);
    }
}
