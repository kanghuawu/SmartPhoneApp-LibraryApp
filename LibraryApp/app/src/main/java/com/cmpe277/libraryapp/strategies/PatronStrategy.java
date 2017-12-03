package com.cmpe277.libraryapp.strategies;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cmpe277.libraryapp.BookListActivity;
import com.cmpe277.libraryapp.LandingPageActivity;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;

import static com.cmpe277.libraryapp.DBHelper.rentBook;
import static com.cmpe277.libraryapp.LoginActivity.EMAIL;
import static com.cmpe277.libraryapp.LoginActivity.IS_LIBRARIAN;
import static com.cmpe277.libraryapp.LoginActivity.LIB_PREFS;

/**
 * Created by bondk on 12/2/17.
 */

public class PatronStrategy extends UserStrategy {
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

    @Override
    public void setUpDetailPage(final Activity activity, final DatabaseReference databaseReference, final Book book) {
        renderBookDetail(activity, book);
        toggleEditable(activity, false);

        Button form_button1 = activity.findViewById(R.id.form_button1);
        form_button1.setText("Rent");
        form_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Renting a book");
                SharedPreferences prefs = activity.getSharedPreferences(LIB_PREFS, 0);
                String email = prefs.getString(EMAIL, "");
                if (!email.equals("")) {
                    rentBook(databaseReference, book, email);
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
}
