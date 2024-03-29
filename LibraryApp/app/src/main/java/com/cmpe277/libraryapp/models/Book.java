package com.cmpe277.libraryapp.models;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by bondk on 12/1/17.
 */

public class Book implements Serializable {
    private String author = "";
    private String title = "";
    private String callNumber  = "";
    private String publisher = "";
    private String yearOfPublication = "";
    private String locationInLibrary = "";
    private int numOfCopies = 1;
    private String currentStatus = "";
    private String keywords = "";
    private String coverImage = "";
    private String borrowTime = "";  // if extended, this will be the time of the first borrow record
    private int numOfExtension = 0;     // [0, 2]
    private String currentBorrower = "";
    private List<String> waitList = new ArrayList<>();

    public String getCurrentBorrower() {
        return currentBorrower;
    }

    public void setCurrentBorrower(String currentBorrower) {
        this.currentBorrower = currentBorrower;
    }

    public List<String> getWaitList() {
        return waitList;
    }

    public void setWaitList(List<String> waitList) {
        this.waitList = waitList;
    }

    public void addToWaitList(String email) {
        this.waitList.add(email);
    }

    public boolean isOnWaitList(String email) {
        return this.waitList.contains(email);
    }

    public static Book fromJson(JSONObject jsonObject) {
        try {
            Book book = new Book();
            JSONObject item = jsonObject.getJSONArray("items").getJSONObject(0);
            JSONObject volumeInfo = item.getJSONObject("volumeInfo");
            book.title = volumeInfo.getString("bookTitle");
            book.author = volumeInfo.getJSONArray("authors").join(", ");
            book.publisher = volumeInfo.getString("publisher");
            book.yearOfPublication = volumeInfo.getString("publishedDate");
            Log.i("LibraryApp", "item to string" + jsonObject.toString());
            Log.i("LibraryApp", "Generated book: " + book.toString());
            return book;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Book() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(String yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    public String getLocationInLibrary() {
        return locationInLibrary;
    }

    public void setLocationInLibrary(String locationInLibrary) {
        this.locationInLibrary = locationInLibrary;
    }

    public int getNumOfCopies() {
        return numOfCopies;
    }

    public void setNumOfCopies(int numOfCopies) {
        this.numOfCopies = numOfCopies;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(String borrowTime) {
        this.borrowTime = borrowTime;
    }

    public int getNumOfExtension() {
        return numOfExtension;
    }

    public void setNumOfExtension(int numOfExtension) {
        this.numOfExtension = numOfExtension;
    }

    public String getDueTime() {

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date borrowDate = null;
        Calendar c = Calendar.getInstance();

        try {
            borrowDate = format.parse(borrowTime);

            c.setTime(borrowDate);
            c.add(Calendar.DATE, (numOfExtension + 1) * 30);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(borrowDate != null) {
            return format.format(c.getTime());
        }

        return "";
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", bookTitle='" + title + '\'' +
                ", callNumber='" + callNumber + '\'' +
                ", publisher='" + publisher + '\'' +
                ", yearOfPublication='" + yearOfPublication + '\'' +
                ", locationInLibrary='" + locationInLibrary + '\'' +
                ", numOfCopies=" + numOfCopies +
                ", currentStatus='" + currentStatus + '\'' +
                ", keywords='" + keywords + '\'' +
                ", borrowTime='" + borrowTime + '\'' +
                ", numOfExtension='" + numOfExtension + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Book that = (Book) obj;
        return that.title.equals(this.title) && that.author.equals(this.author)
                && that.callNumber.equals(this.callNumber)
                && that.publisher.equals(this.publisher)
                && that.yearOfPublication.equals(this.yearOfPublication)
                && that.locationInLibrary.equals(this.locationInLibrary)
                && that.numOfCopies == this.numOfCopies
                && that.currentStatus.equals(this.currentStatus)
                && that.keywords.equals(this.keywords)
                && that.borrowTime.equals(this.borrowTime)
                && that.numOfExtension == this.numOfExtension;
    }
}