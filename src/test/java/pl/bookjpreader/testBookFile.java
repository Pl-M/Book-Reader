/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import pl.bookjpreader.booksfactory.BookFile;
import pl.bookjpreader.booksfactory.BookFile.Reader;

public class testBookFile extends TestCase {
    private String book;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        book = MockBook.getBook();
    }

    @Test
    public void testNext(){
        Reader reader = new BookFile.Reader(book);

        StringBuilder newBook = new StringBuilder();
        int startPosition = 0;
        while (startPosition < book.length()){
            int length = (int)(Math.random() * 1000);
            String s = reader.getNextBlock(startPosition, length);
            newBook.append(s);
            startPosition += s.length();
        }
        assertTrue(newBook.length() > 1);
        assertEquals(book, newBook.toString());
    }
    @Test
    public void testPrevious(){
        Reader reader = new BookFile.Reader(book);

        StringBuilder newBook = new StringBuilder();
        int endPosition = book.length();
        while (endPosition > 0){
            int length = (int)(Math.random() * 1000);
            String s = reader.getPreviousBlock(endPosition, length);
            newBook.insert(0, s);
            endPosition -= s.length();
        }
        assertTrue(newBook.length() > 1);
        assertEquals(book, newBook.toString());
    }
}
