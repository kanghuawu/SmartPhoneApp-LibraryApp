package com.cmpe277.libraryapp.strategies;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmpe277.libraryapp.BookCreateActivity;
import com.cmpe277.libraryapp.GMailSender;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;

import static com.cmpe277.libraryapp.DBHelper.addOrUpdateNewBookToDB;

/**
 * Created by bondk on 12/2/17.
 */

public abstract class UserStrategy {
    boolean toggleEdible = false;
    public abstract void setUpLandingPage(Activity activity);
    public abstract void setUpDetailPage(Activity activity, DatabaseReference databaseReference, Book book);
    public void setUpBookCreatingPage(final Activity activity, final DatabaseReference databaseReference) {
        TextView status = activity.findViewById(R.id.book_status);
        EditText statusEdit = activity.findViewById(R.id.book_status_edit);
        status.setVisibility(View.GONE);
        statusEdit.setVisibility(View.GONE);
        final Button detail_button1 = activity.findViewById(R.id.form_button1);
        detail_button1.setText("Create");
        detail_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = prepareUpdate(activity);
                addOrUpdateNewBookToDB(databaseReference, book);
                activity.finish();
            }
        });

        Button detail_button2 = activity.findViewById(R.id.form_button2);
        detail_button2.setVisibility(View.INVISIBLE);
    }

    void renderBookDetail(Activity activity, Book book) {
        EditText title = activity.findViewById(R.id.book_title_edit);
        title.setText(book.getTitle());
        EditText author = activity.findViewById(R.id.book_author_edit);
        author.setText(book.getAuthor());
        EditText callnum = activity.findViewById(R.id.book_callnum_edit);
        callnum.setText(book.getCallNumber());
        callnum.setFocusable(false);
        EditText publisher = activity.findViewById(R.id.book_publisher_edit);
        publisher.setText(book.getPublisher());
        EditText year = activity.findViewById(R.id.book_year_edit);
        year.setText(book.getYearOfPublication());
        EditText location = activity.findViewById(R.id.book_location_edit);
        location.setText(book.getLocationInLibrary());
        EditText status = activity.findViewById(R.id.book_status_edit);
        status.setText(book.getCurrentStatus());
        status.setFocusable(false);
        EditText keywords = activity.findViewById(R.id.book_keywords_edit);
        keywords.setText(book.getKeywords());
    }



    Book prepareUpdate(Activity activity) {
        Book book = new Book();
        String title = ((EditText)activity.findViewById(R.id.book_title_edit)).getText().toString();
        String author = ((EditText)activity.findViewById(R.id.book_author_edit)).getText().toString();
        String callnum = ((EditText)activity.findViewById(R.id.book_callnum_edit)).getText().toString();
        String publisher = ((EditText)activity.findViewById(R.id.book_publisher_edit)).getText().toString();
        String year = ((EditText)activity.findViewById(R.id.book_year_edit)).getText().toString();
        String location = ((EditText)activity.findViewById(R.id.book_location_edit)).getText().toString();
        String status = ((EditText)activity.findViewById(R.id.book_status_edit)).getText().toString();
        String keywords = ((EditText)activity.findViewById(R.id.book_keywords_edit)).getText().toString();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCallNumber(callnum);
        book.setPublisher(publisher);
        book.setYearOfPublication(year);
        book.setLocationInLibrary(location);
        book.setCurrentStatus(status);
        book.setKeywords(keywords);
        return book;
    }
    void toggleEditable(Activity activity) {
        EditText title = activity.findViewById(R.id.book_title_edit);
        EditText author = activity.findViewById(R.id.book_author_edit);
        EditText publisher = activity.findViewById(R.id.book_publisher_edit);
        EditText year = activity.findViewById(R.id.book_year_edit);
        EditText location = activity.findViewById(R.id.book_location_edit);
        EditText keywords = activity.findViewById(R.id.book_keywords_edit);
        title.setFocusableInTouchMode(toggleEdible);
        author.setFocusableInTouchMode(toggleEdible);
        publisher.setFocusableInTouchMode(toggleEdible);
        year.setFocusableInTouchMode(toggleEdible);
        location.setFocusableInTouchMode(toggleEdible);
        keywords.setFocusableInTouchMode(toggleEdible);
        title.setFocusable(toggleEdible);
        author.setFocusable(toggleEdible);
        publisher.setFocusable(toggleEdible);
        year.setFocusable(toggleEdible);
        location.setFocusable(toggleEdible);
        keywords.setFocusable(toggleEdible);
        toggleEdible  = !toggleEdible;
    }

    public void sendEmailNotification(String email, String content) {
        try {
            //Set your email user name and password
            Log.i("LibraryApp","---Send Email Notification to: " + email);
            GMailSender sender = new GMailSender("cmpe277library@gmail.com", "sjsu277123456");
            sender.sendMail("LibraryApp Notification",
                    content,
                    "cmpe277library@gmail.com",
                    email);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
}
