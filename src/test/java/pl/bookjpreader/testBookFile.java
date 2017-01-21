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
        /*
         * Test that getNextBlock doesn't add new fragments or miss any part of the book.
         */
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
        /*
         * Test that getPreviousBlock doesn't add new fragments or miss any part of the book.
         */
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
    @Test
    public void testPercentConversion(){
        /*
         * Test that conversion from offset to percent and back gives the
         * same result.
         */
        Reader r = new BookFile.Reader(book);

        int pos = 0;
        for (int i = 4; i > 0; i--){
            assertEquals(pos, r.getOffsetFromPercent(r.getPercentFromOffset(pos)));
            pos = r.textLength/i;
            assertTrue(pos > 0); // just to be sure
        }
     }
    @Test
    public void testPosRound(){
        /*
         * Test posRoundLeft and posRoundRight functions. 
         */
        Reader r = new BookFile.Reader("text with spaces");

        assertEquals(r.posRoundLeft(7, 1), 7);
        assertEquals(r.posRoundLeft(7, 0), 7);
        assertEquals(r.posRoundLeft(7, 5), 4);
        assertEquals(r.posRoundLeft(7, 25), 4);

        assertEquals(r.posRoundRight(7, 1), 7);
        assertEquals(r.posRoundRight(7, 0), 7);
        assertEquals(r.posRoundRight(7, 5), 9);
        assertEquals(r.posRoundRight(7, 25), 9);
        
    }
}
