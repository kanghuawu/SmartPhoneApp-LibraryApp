package com.cmpe277.libraryapp;

import android.app.ListActivity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmpe277.libraryapp.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bondk on 12/2/17.
 */

public class BookListAdaptor extends BaseAdapter {
    private ListActivity mActivity;
    private ArrayList<Book> mBooks;
    private ArrayList<Book> mBooksAll;


    public BookListAdaptor(ListActivity activity, ArrayList<Book> book) {
        mActivity = activity;
        mBooks = book;
        mBooksAll = book;
    }

    static class ViewHolder {
        TextView author;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mBooks.size();
    }

    @Override
    public Book getItem(int position) {
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.book_list_row, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.author = convertView.findViewById(R.id.author);
            holder.body =  convertView.findViewById(R.id.title);
            holder.params = (LinearLayout.LayoutParams) holder.author.getLayoutParams();
            convertView.setTag(holder);
        }

        final Book book = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        String author = book.getAuthor();
        holder.author.setText(author);


        String msg = book.getTitle();
        holder.body.setText(msg);

        return convertView;
    }
    public void filter(String charText) {
        charText = charText.toLowerCase();
        ArrayList<Book> temp = new ArrayList<Book>();
        //mBooks.clear();
        if (charText.length() == 0) {
            //mBooks.addAll(temp);
            mBooks = mBooksAll;
        }
        else
        {
            for (Book wp : mBooksAll)
            {
                if (wp.getTitle().toLowerCase().contains(charText))
                {
                    temp.add(wp);
                }
            }
            mBooks = temp;
            Log.i("mBooks size ", ""+mBooks.size());
        }
        notifyDataSetChanged();
    }
}
