package com.cmpe277.libraryapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bondk on 12/2/17.
 */

public class DBHelper {
    public static final String USER_DB = "user";
    public static final String BOOK_DB = "book";

    public static final String BOOK_STATUS = "currentStatus";
    public static final String BOOK_STATUS_RENT = "Rent";

    public static final String BOOK_BORROW_Time = "borrowTime";
    public static final String TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";

    public static final String NUM_OF_EXTENSION = "numOfExtension";


    public static void getAllBookListFromDB(DatabaseReference databaseReference, final BaseAdapter adapter, final ArrayList<Book> books) {
        databaseReference.child(BOOK_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i("&&&", child.getKey());
                    if(!child.getKey().contains("@")) {
                        books.add(child.getValue(Book.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getMyBookListFromDB(DatabaseReference databaseReference, final BaseAdapter adapter, final ArrayList<Book> books, String email) {
        email = email.replace(".", "dot");
        databaseReference.child(USER_DB).child(email).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static String removeBookFromDB(DatabaseReference databaseReference, Book book) {
        String feedback = "";

        Log.i("DEBUG", "book " + book.getTitle() + " status: " + book.getCurrentStatus());

        if(!book.getCurrentStatus().equals(BOOK_STATUS_RENT)) {
            databaseReference.child(BOOK_DB).child(book.getCallNumber()).removeValue();
        } else {
            Log.i("ERROR", "Cannot delete a book that's already been rent to users");
            feedback = "Cannot delete a book in 'Rent' status";
        }

        return feedback;
    }

    public static String rentBook(DatabaseReference databaseReference, Book book, String email) {
        String feedback = "";

        if(!book.getCurrentStatus().equals(BOOK_STATUS_RENT)) {
            Log.i("INFO", "Book " + book.getTitle() +  " is available.");

            email = email.replace(".", "dot");
            String curTime = new SimpleDateFormat(TIME_FORMAT).format(new Date());

            databaseReference.child(USER_DB).child(email)
                    .child(book.getCallNumber())
                    .setValue(book);
            databaseReference.child(USER_DB).child(email)
                    .child(book.getCallNumber())
                    .child(BOOK_STATUS)
                    .setValue(BOOK_STATUS_RENT);
            databaseReference.child(USER_DB).child(email)
                    .child(book.getCallNumber())
                    .child(BOOK_BORROW_Time)
                    .setValue(curTime);
            databaseReference.child(BOOK_DB)
                    .child(book.getCallNumber())
                    .child(BOOK_STATUS)
                    .setValue(BOOK_STATUS_RENT);
            databaseReference.child(BOOK_DB)
                    .child(book.getCallNumber())
                    .child(BOOK_BORROW_Time)
                    .setValue(curTime);

            book.setBorrowTime(curTime);

            Log.i("INFO", "Set book " + book.getTitle() +  " borrow time to be: " + curTime);
        } else {
            Log.i("INFO", "Book " + book.getTitle() + " is not available");
            feedback = "Book is not available";
        }

        return feedback;
    }

    public static String extendBook(DatabaseReference databaseReference, Book book, String email) {
        String feedback = "";

        email = email.replace(".", "dot");
        if(book.getCurrentStatus().equals("Rent")) {
            Log.i("INFO", "Book " + book.getTitle() + " can be extended.");

            if(book.getNumOfExtension() < 2) {
                int curNumOfExtension = book.getNumOfExtension();
                Log.i("DEBUG", "cur numOfExtension: " + book.getNumOfExtension());

                databaseReference.child(USER_DB).child(email)
                        .child(book.getCallNumber())
                        .child(NUM_OF_EXTENSION)
                        .setValue(curNumOfExtension + 1);
                databaseReference.child(BOOK_DB)
                        .child(book.getCallNumber())
                        .child(NUM_OF_EXTENSION)
                        .setValue(curNumOfExtension + 1);
            } else {
                Log.i("INFO", "Book " + book.getTitle() +  " has already been extended twice.");
                feedback = "Book already been extended twice.";
            }
        } else {
            Log.i("INFO", "Cannot extend a book that's not rent by you.");
            feedback = "Cannot extend a book that's not rent by you.";
        }

        return feedback;
    }

    public static void returnBook(DatabaseReference databaseReference, Book book, String email) {
        email = email.replace(".", "dot");
        databaseReference.child(USER_DB).child(email)
                .child(book.getCallNumber())
                .removeValue();
        databaseReference.child(BOOK_DB)
                .child(book.getCallNumber())
                .child(BOOK_STATUS)
                .setValue("");
        databaseReference.child(BOOK_DB)
                .child(book.getCallNumber())
                .child(BOOK_BORROW_Time)
                .setValue("");
    }

    public static void forDemo(DatabaseReference databaseReference, Book book, String email) {
        email = email.replace(".", "dot");

        String borrowTime = book.getBorrowTime();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String fakeBorrowTime = "";

        try {
            Date borrowDate = format.parse(borrowTime);
            c.setTime(borrowDate);
            c.add(Calendar.DATE, -29);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        fakeBorrowTime = format.format(c.getTime());


        databaseReference.child(USER_DB).child(email)
                .child(book.getCallNumber())
                .child(BOOK_BORROW_Time)
                .setValue(fakeBorrowTime);
        databaseReference.child(BOOK_DB)
                .child(book.getCallNumber())
                .child(BOOK_BORROW_Time)
                .setValue(fakeBorrowTime);

        book.setBorrowTime(fakeBorrowTime);
    }
}

