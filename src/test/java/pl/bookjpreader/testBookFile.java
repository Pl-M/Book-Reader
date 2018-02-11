/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.bookjpreader.booksfactory.BookHandler;

import java.nio.file.Paths;

public class testBookFile {
    private String text;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        text = MockBook.getBook();
    }

    /**
     * Tests that getNextBlock doesn't add new fragments or miss any part of the text.
     */
    @Test
    public void testNext(){
        final BookHandler handler = new BookHandler(Paths.get(""), text);

        StringBuilder newBook = new StringBuilder();
        int startPosition = 0;
        while (startPosition < text.length()){
            int length = (int)(Math.random() * 1000);
            String s = handler.getNextBlock(startPosition, length);
            newBook.append(s);
            startPosition += s.length();
        }
        Assert.assertTrue(newBook.length() > 1);
        Assert.assertEquals(text, newBook.toString());
    }
    /**
     * Tests that getPreviousBlock doesn't add new fragments or miss any part of the text.
     */
    @Test
    public void testPrevious(){
        final BookHandler handler = new BookHandler(Paths.get(""), text);

        StringBuilder newBook = new StringBuilder();
        int endPosition = text.length();
        while (endPosition > 0){
            int length = (int)(Math.random() * 1000);
            String s = handler.getPreviousBlock(endPosition, length);
            newBook.insert(0, s);
            endPosition -= s.length();
        }
        Assert.assertTrue(newBook.length() > 1);
        Assert.assertEquals(text, newBook.toString());
    }
    /**
     * Tests that conversion from offset to percent and back gives the
     * same result.
     */
    @Test
    public void testPercentConversion(){
        final BookHandler handler = new BookHandler(Paths.get(""), text);

        int pos = 0;
        for (int i = 4; i > 0; i--){
            Assert.assertEquals(pos, handler.getOffsetFromPercent(handler.getPercentFromOffset(pos)));
            pos = handler.getTextLength()/i;
            Assert.assertTrue(pos > 0); // just to be sure
        }
     }
    /**
     * Tests posRoundLeft and posRoundRight functions.
     */
    @Test
    public void testPosRound(){
        final BookHandler handler =
                new BookHandler(Paths.get(""), "text with spaces");


        Assert.assertEquals(handler.posRoundLeft(7, 1), 7);
        Assert.assertEquals(handler.posRoundLeft(7, 0), 7);
        Assert.assertEquals(handler.posRoundLeft(7, 5), 4);
        Assert.assertEquals(handler.posRoundLeft(7, 25), 4);

        Assert.assertEquals(handler.posRoundRight(7, 1), 7);
        Assert.assertEquals(handler.posRoundRight(7, 0), 7);
        Assert.assertEquals(handler.posRoundRight(7, 5), 9);
        Assert.assertEquals(handler.posRoundRight(7, 25), 9);

    }
}
