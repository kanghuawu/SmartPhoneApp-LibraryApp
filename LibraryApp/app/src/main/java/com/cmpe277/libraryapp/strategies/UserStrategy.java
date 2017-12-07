package com.cmpe277.libraryapp.strategies;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe277.libraryapp.BookCreateActivity;
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

    void renderBookDetailSubset(Activity activity, Book book) {
        EditText title = activity.findViewById(R.id.book_title_edit);
        title.setText(book.getTitle());
        EditText author = activity.findViewById(R.id.book_author_edit);
        author.setText(book.getAuthor());
        EditText publisher = activity.findViewById(R.id.book_publisher_edit);
        publisher.setText(book.getPublisher());
        EditText year = activity.findViewById(R.id.book_year_edit);
        year.setText(book.getYearOfPublication());
    }


    Book prepareUpdate(Activity activity) {
        Book book = new Book();
        String title = ((EditText)activity.findViewById(R.id.book_title_edit)).getText().toString();
        String author = ((EditText)activity.findViewById(R.id.book_author_edit)).getText().toString();
        String callnum = ((EditText)activity.findViewById(R.id.book_callnum_edit)).getText().toString();
        if (title.equals("") || author.equals("") || callnum.equals("")) {
            Toast.makeText(activity, "Need title/call num/author", Toast.LENGTH_SHORT).show();
            return null;
        }
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
}
