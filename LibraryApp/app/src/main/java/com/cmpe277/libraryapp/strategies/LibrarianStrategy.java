package com.cmpe277.libraryapp.strategies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmpe277.libraryapp.BookCreateActivity;
import com.cmpe277.libraryapp.BookListActivity;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;

import static com.cmpe277.libraryapp.DBHelper.addOrUpdateNewBookToDB;
import static com.cmpe277.libraryapp.DBHelper.removeBookFromDB;

/**
 * Created by bondk on 12/2/17.
 */

public class LibrarianStrategy extends UserStrategy  {

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
        landing_button2.setText("Create New Book");
        landing_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent formIntent = new Intent(activity, BookCreateActivity.class);
                activity.startActivity(formIntent);
            }
        });
    }


    @Override
    public void setUpDetailPage(final Activity activity, final DatabaseReference databaseReference, final Book book) {
        renderBookDetail(activity, book);
        toggleEditable(activity);
        final Button form_button1 = activity.findViewById(R.id.form_button1);
        form_button1.setText("Edit");
        form_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "About to edit a book");
                if (toggleEdible){
                    form_button1.setText("Save");
                    form_button1.setTextColor(Color.RED);

                } else {
                    form_button1.setText("Edit");
                    form_button1.setTextColor(Color.BLACK);
                    Book newBook = prepareUpdate(activity);
                    if (!newBook.equals(book)) {
                        Log.i("LibraryApp", "newBook " + newBook.toString());
                        Log.i("LibraryApp", "book    " + book.toString());
                        addOrUpdateNewBookToDB(databaseReference, newBook);
                    }
                }
                toggleEditable(activity);
                Log.i("LibraryApp", String.valueOf(toggleEdible));
            }
        });
        Button form_button2 = activity.findViewById(R.id.form_button2);
        form_button2.setText("Delete");
        form_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Deleting a book");
                removeBookFromDB(databaseReference, book.getCallNumber());
                activity.finish();
            }
        });
    }


}
