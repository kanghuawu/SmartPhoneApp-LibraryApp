package com.cmpe277.libraryapp.strategies;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmpe277.libraryapp.BookListActivity;
import com.cmpe277.libraryapp.GMailSender;
import com.cmpe277.libraryapp.LandingPageActivity;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;

import static com.cmpe277.libraryapp.DBHelper.addOrUpdateNewBookToDB;
import static com.cmpe277.libraryapp.DBHelper.extendBook;
import static com.cmpe277.libraryapp.DBHelper.removeBookFromDB;
import static com.cmpe277.libraryapp.DBHelper.rentBook;
import static com.cmpe277.libraryapp.DBHelper.returnBook;
import static com.cmpe277.libraryapp.LoginActivity.EMAIL;
import static com.cmpe277.libraryapp.LoginActivity.IS_LIBRARIAN;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

/**
 * Created by bondk on 12/2/17.
 */

public class PatronStrategy extends UserStrategy {

    public static final String USER_DB = "user";
    public static final String BOOK_DB = "book";

    public static final String BOOK_STATUS = "status";
    public static final String BOOK_STATUS_RENT = "Rent";
    public static final String BOOK_STATUS_AVAILABLE = "Available";

    @Override
    public void setUpLandingPage(final Activity activity) {
        Button landing_button1 = activity.findViewById(R.id.landing_button1);
        landing_button1.setText("Book List");
        landing_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "button one clicked");
                Intent formIntent = new Intent(activity, BookListActivity.class);
                activity.startActivity(formIntent);
            }
        });

        Button landing_button2 = activity.findViewById(R.id.landing_button2);
        landing_button2.setText("My Book List");
        landing_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:
                Intent formIntent = new Intent(activity, BookListActivity.class);
                formIntent.putExtra("myBookList", true);
                activity.startActivity(formIntent);
            }
        });
    }

    // detail view for BOOKLIST. contains only one button "RENT"
    @Override
    public void setUpDetailPage(final Activity activity, final DatabaseReference databaseReference, final Book book) {
        Log.i("patron  line69&&&", "status " + book.getCurrentStatus());
        renderBookDetail(activity, book);
        toggleEditable(activity);

        Button form_button1 = activity.findViewById(R.id.form_button1);
        form_button1.setText("Rent");
        form_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Renting a book");
                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                final String email = prefs.getString(EMAIL, "");
//                Log.i("LibraryApp",email);
                if (!email.equals("")) {
                    //Send email notification when rent a book
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String emailContent = "Hi!\nThe Book:\n" + book.getTitle() +
                                    " has been rent\nBorrow time: " + book.getBorrowTime() +
                                    "\nThank you!\n\nLibrary Team";
                            sendEmailNotification(email, emailContent);
                        }
                    }).start();

                    String feedback = rentBook(databaseReference, book, email);
                    if(!feedback.equals("")) {
                        Toast.makeText(activity, feedback,
                                Toast.LENGTH_LONG).show();
                    }

                    Intent intentMyList = new Intent(activity, BookListActivity.class);
                    activity.setResult(45);
                    activity.finish();
                    activity.startActivity(intentMyList);
                }
            }
        });
        Button form_button2 = activity.findViewById(R.id.form_button2);
        form_button2.setVisibility(View.GONE);
    }

    // detail view for MY BOOKLIST. contains two buttons "RETURN" and "EXTEND"
    public void setUpDetailPageForMyList(final Activity activity, final DatabaseReference databaseReference, final Book book) {
        renderBookDetail(activity, book);
        toggleEditable(activity);

        Button form_button1 = activity.findViewById(R.id.form_button1);
        form_button1.setText("Return");
        form_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Returning a book");
                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                final String email = prefs.getString(EMAIL, "");
                if (!email.equals("")) {

                    returnBook(databaseReference, book, email);

                    // Send an email notification after return
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String emailContent = "Hi!\nThe Book:\n" + book.getTitle() + " has been return\n\nThank you!\n\nLibrary Team";
                            sendEmailNotification(email, emailContent);
                        }
                    }).start();

                    Intent intentMyList = new Intent(activity, BookListActivity.class);
                    activity.setResult(45);
                    activity.finish();
                    activity.startActivity(intentMyList);
                }
            }
        });
        Button form_button2 = activity.findViewById(R.id.form_button2);

        form_button2.setText("Extend");
        form_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Extending book " + book.getTitle());

                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                String email = prefs.getString(EMAIL, "");

                if (!email.equals("")) {

                    String feedback = extendBook(databaseReference, book, email);
                    if(!feedback.equals("")) {
                        Toast.makeText(activity, feedback,
                                Toast.LENGTH_LONG).show();
                    }

                    Intent intentMyList = new Intent(activity, BookListActivity.class);
                    activity.setResult(45);
                    activity.finish();
                    activity.startActivity(intentMyList);
                }

            }
        });
    }

}
