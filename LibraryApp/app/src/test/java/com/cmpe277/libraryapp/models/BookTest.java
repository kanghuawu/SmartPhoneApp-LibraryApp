package com.cmpe277.libraryapp.models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by laurazhou on 12/4/17.
 */
public class BookTest {
    Book testBook = new Book();

    @Test
    public void toStringTest() {
        System.out.println(testBook.toString());
    }
}