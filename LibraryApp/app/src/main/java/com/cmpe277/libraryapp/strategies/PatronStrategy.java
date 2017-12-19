package com.cmpe277.libraryapp.strategies;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cmpe277.libraryapp.BookListActivity;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;

import static com.cmpe277.libraryapp.DBHelper.extendBook;
import static com.cmpe277.libraryapp.DBHelper.forDemo;
import static com.cmpe277.libraryapp.DBHelper.putOnWaitList;
import static com.cmpe277.libraryapp.DBHelper.rentBook;
import static com.cmpe277.libraryapp.DBHelper.returnBook;
import static com.cmpe277.libraryapp.LoginActivity.EMAIL;
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

    // detail view for BOOKLIST.
    @Override
    public void setUpDetailPage(final Activity activity, final DatabaseReference databaseReference, final Book book) {
        Log.i("patron  line69&&&", "status " + book.getCurrentStatus());
        renderBookDetail(activity, book);
        toggleEditable(activity);

        Button form_button1 = activity.findViewById(R.id.form_button1);
        form_button1.setText("Borrow");
        form_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Renting a book");
                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                final String email = prefs.getString(EMAIL, "");

                Alert alert = null;
                if (!email.equals("")) {
                    if (book.getCurrentStatus().equals(BOOK_STATUS_RENT)) {
                        if (book.getCurrentBorrower().equals(email)) {
                            showAlert("You should have the book.", activity);
                            return;
                        }
                        if (book.getWaitList().contains(email)) {
                            showAlert("You are on waiting list. Please wait patiently.", activity);
                            return;
                        }
                        alert = new WaitListAlert();
                    } else {
                        alert = new RentAlert();
                    }

                    if (!book.getWaitList().contains(email) || book.getWaitList().get(0).equals(email)) {
                        alert.book = book;
                        alert.email = email;
                        alert.databaseReference = databaseReference;
                        showAlert(alert, activity);
                    } else {
                        showAlert("Seems someone is ahead of you on waiting list.", activity);
                    }
                }
            }
        });
        Button form_button2 = activity.findViewById(R.id.form_button2);
        form_button2.setVisibility(View.GONE);

        Button form_button3 = activity.findViewById(R.id.form_button3);
        form_button3.setVisibility(View.GONE);
    }

    private abstract class Alert {
        protected Book book;
        protected DatabaseReference databaseReference;
        protected String email;
        abstract String getTitle();
        abstract String getMessage();
        abstract void clickOk();
    }

    private class RentAlert extends Alert {

        @Override
        public String getTitle() {
            return "Please confirm";
        }

        @Override
        public String getMessage() {
            return "Are you sure you want to borrow " + this.book.getTitle() + ". " +
                    "We will send you a confirmation email.";
        }

        @Override
        public void clickOk() {
            rentBook(this.databaseReference, this.book, this.email);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String emailContent = "Hi!" +
                            "\nThe Book:\n" +
                            book.getTitle() + " has been borrowed" +
                            "\nBorrow time: " + book.getBorrowTime() +
                            "\ndue date: " + book.getDueTime() +
                            "\nThank you!\n" +
                            "\nLibrary Team";
                    sendEmailNotification(email, emailContent);
                }
            }).start();
        }

    }

    private class WaitListAlert extends Alert {

        @Override
        public String getTitle() {
            return "Oop...";
        }

        @Override
        public String getMessage() {
            return this.book.getTitle() + " is not in library. " +
                    "Would you like to be on waiting list? " +
                    "You will be number " + String.valueOf(book.getWaitList().size() + 1) + ".";
        }

        @Override
        public void clickOk() {
            putOnWaitList(this.databaseReference, this.book, this.email);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String emailContent = "Hi!" +
                            "\nThe Book:\n" +
                            book.getTitle() + " is not available at this time" +
                            "\nYou are number " + book.getWaitList().size() + " on waiting list" +
                            "\nWe will let you know when the book is available." +
                            "\nThank you!\n" +
                            "\nLibrary Team";
                    sendEmailNotification(email, emailContent);
                }
            }).start();
        }
    }

    private void showAlert(final Alert alert, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(alert.getTitle());
        builder.setMessage(alert.getMessage());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("LibraryApp", "alertdialog clicked ok");
                alert.clickOk();
                returnToLandingPage(activity);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("LibraryApp", "alertdialog clicked cancel");
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlert(String msg, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Hmm...");
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("LibraryApp", "alertdialog clicked ok");
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void returnToLandingPage(Activity activity) {
        Intent intentMyList = new Intent(activity, BookListActivity.class);
        activity.setResult(45);
        activity.finish();
        activity.startActivity(intentMyList);
    }

    // detail view for MY BOOKLIST.
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
                            String emailContent = "Hi!\n" +
                                    "The Book:\n" +
                                    book.getTitle() + " has been returned." +
                                    "\n\nThank you!\n\nLibrary Team";
                            sendEmailNotification(email, emailContent);
                        }
                    }).start();


                    if (book.getWaitList().size() > 0) {
                        final String nextEmail = book.getWaitList().get(0);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("LibraryApp", "Sending second email to: " + nextEmail);
                                String emailContent = "Hi!" +
                                        "\nThe Book:\n" +
                                        book.getTitle() + " is available to pick up." +
                                        "\n\nThank you!" +
                                        "\n\nLibrary Team";
                                sendEmailNotification(nextEmail, emailContent);
                            }
                        }).start();
                    }

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


        Button form_button3 = activity.findViewById(R.id.form_button3);
        form_button3.setText("Fake Date");
        form_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO", "FOR DEMO");

                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                final String email = prefs.getString(EMAIL, "");
                
                forDemo(databaseReference, book, email);

                //Send email notification after rent a book
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String emailContent = "Hi!\nThe Book:\n" + book.getTitle() +
                                " left 1 day to return!" +
                                "\nThe borrow time of this book is:" + book.getBorrowTime() +
                                "\nThank you!\n\nLibrary Team";
                        sendEmailNotification(email, emailContent);
                    }
                }).start();

                Intent intentMyList = new Intent(activity, BookListActivity.class);
                activity.setResult(45);
                activity.finish();
                activity.startActivity(intentMyList);
            }
        });
    }

}
