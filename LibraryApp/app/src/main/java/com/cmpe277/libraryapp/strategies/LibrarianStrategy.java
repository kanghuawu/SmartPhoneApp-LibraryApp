package com.cmpe277.libraryapp.strategies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe277.libraryapp.BookCreateActivity;
import com.cmpe277.libraryapp.BookListActivity;
import com.cmpe277.libraryapp.R;
import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DatabaseReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

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

                if (book != null) {
                    addOrUpdateNewBookToDB(databaseReference, book);
                    activity.finish();
                }
            }
        });

        final EditText  callnumEdit = activity.findViewById(R.id.book_callnum_edit);

        Button detail_button2 = activity.findViewById(R.id.form_button2);
        detail_button2.setText("Search");
        detail_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LibraryApp", "Searching for book");
                String isbn = callnumEdit.getText().toString();
                if (isbn.equals("")) {
                    Toast.makeText(activity, "Need call num for searching", Toast.LENGTH_SHORT).show();
                    return ;
                }
                Log.i("LibraryApp", "isbn length " + String.valueOf(isbn.length()));
                if (!(isbn.length() == 10 || isbn.length() == 13)) {
                    Toast.makeText(activity, "Call num length isn't correct", Toast.LENGTH_SHORT).show();
                    return ;
                }
                Log.i("LibraryApp", "isbn " + isbn);
                searchBookOnline(activity, isbn);
            }
        });
    }

    private final String GOOGLE_URL = "https://www.googleapis.com/books/v1/volumes";
    private final String KEY = "AIzaSyCqd2Lqzvf9raparg9v61t6GyxWL_aK3Mg";

    private void searchBookOnline(final Activity activity, String isbn) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "isbn:"+isbn);
        params.put("key", KEY);
        client.get(GOOGLE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("LibraryApp", "Succes! JSON: " + response.toString());
                try{
                    int totalItems = response.getInt("totalItems");
                    if (totalItems == 0) {
                        Toast.makeText(activity, "Cannot find your bood", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    Book bookData = Book.fromJson(response);
                    Log.i("LibraryApp", bookData.toString());
                    renderBookDetailSubset(activity, bookData);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("LibraryApp", "Fail " + throwable.toString());
                Log.e("LibraryApp", "Response " + errorResponse.toString());
                Log.e("LibraryApp", "Status code " + statusCode);
                Toast.makeText(activity, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
