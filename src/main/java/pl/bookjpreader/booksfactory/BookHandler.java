/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.booksfactory;

import java.nio.file.Path;


public class BookHandler {
    /**
     * If paragraph exceeds this limit it will be split.
     */
    final private static int MAX_PARAGRAPH_SIZE = 500;

    final private BookEntity bookEntity;
    final private BookReader reader;

    /**
     * Main constructor.
     * @param bookEntity entity of the book.
     * @throws Exception if an error occurs during opening/encoding the book.
     */
    public BookHandler(final BookEntity bookEntity) throws Exception {
        this.bookEntity = bookEntity;
        reader = new BookReader(bookEntity);
    }
    /**
     * This constructor may be used for tests.
     * @param text: full text of the file.
     * @param filePath: path of the file to read.
     */
    public BookHandler(final Path filePath, String text) {
        bookEntity = new BookEntity(filePath);
        reader = new BookReader(text);
    }

    public BookEntity getEntity() {
        return bookEntity;
    }
    public int getTextLength(){
        return reader.getTextLength();
    }

    /**
     * This function may be used after jumping to a completely new location in the book
     * to prevent startPos to be in the middle of the word.
     * @param startPos: initial start position;
     * @param length: maximum difference between new and start positions.
     * @return a rounded startPosition if can be found or startPos.
     */
    public int posRoundLeft(int startPos, int length){
        // Find the nearest space.
        int spacePos = reader.getText().lastIndexOf(" ", startPos);
        if (spacePos > 0 && startPos - spacePos < length)
            startPos = spacePos;

        return startPos;
    }
    /**
     * This function may be used after jumping to a completely new location in the book
     * to prevent startPos to be in the middle of the word.
     * @param startPos: initial start position;
     * @param length: maximum difference between new and start positions.
     * @return a rounded startPosition if can be found or startPos.
     */
    public int posRoundRight(int startPos, int length){
        // Find the nearest space.
        int spacePos = reader.getText().indexOf(" ", startPos);
        if (spacePos > 0 && spacePos - startPos < length)
            startPos = spacePos;

        return startPos;
    }

    public String getNextBlock(final int startPos){

        return getNextBlock(startPos, 100);
    }
    public String getPreviousBlock(int endPos){
        return getPreviousBlock(endPos, 100);
    }

    /**
     * @param length: block should be bigger than length (this is to eliminate
     * blocks consisting only from one newline).
     * @param startPos: position in the text which is the beginning of the block.
     * @return block of text beginning from the position pos and ending at newline (or
     * at 800th symbol from the beginning if no newline found).
     */
    public String getNextBlock(final int startPos, final int length){
        final int textLength = reader.getTextLength();
        final String text = reader.getText();

        if (startPos < 0 || startPos >= textLength)
            return null;

        if (startPos >= textLength - length)
            return text.substring(startPos, textLength);

        int end = text.indexOf("\n", startPos + length);
        if (end == -1)
            end = textLength;

        int maxEnd = startPos + length + MAX_PARAGRAPH_SIZE;
        if (end > maxEnd) // paragraph is too long
            end = posRoundRight(maxEnd, 100);

        // Include newline to the end.
        return text.substring(startPos, end + 1);
    }

    /**
     * @param length: block should be bigger than length (this is to eliminate
     * blocks consisting only from one newline).
     * @param endPos: position in the text which is the end of the block.
     * @return block of text beginning after newline (or
     * at 800th symbol from the end if no newline found) and ending in pos.
     */
    public String getPreviousBlock(final int endPos, final int length){
        final String text = reader.getText();

        if (endPos <= 0 || endPos > reader.getTextLength())
            return null;

        if (endPos <= length)
            return text.substring(0, endPos);

        int begin = text.lastIndexOf("\n", endPos - length);

        if (begin == -1)
            begin = 0;
        else
            begin += 1; // exclude newline from the beginning

        int minBegin = endPos - length - MAX_PARAGRAPH_SIZE;
        if (begin < minBegin) // paragraph is too long
            begin = posRoundLeft(minBegin, 100);

        return text.substring(begin, endPos);
    }

    /**
     * Function to search text in the book, currently is case-sensitive.
     * @param pattern: text to find,
     * @param fromPercent: initial position from which to start search
     * in percents; search is conducted from fromPos position to the end,
     * @return starting position of the found text in percents,
     * null if the text was not found.
     */
    public Double searchText(String pattern, double fromPercent){
        final int foundPos =
                reader.getText().indexOf(pattern, getOffsetFromPercent(fromPercent));
        return foundPos == -1 ? null : getPercentFromOffset(foundPos);
    }

    /*
     * Functions to convert from percent to position and back.
     */
    public int getOffsetFromPercent(double percent){
        if (percent < 0)
            percent = 0;

        int offset = (int)Math.round(reader.getTextLength() * percent / 100);

        // Due to rounding percent may be more than 100.
        return Math.min(offset, reader.getTextLength());
    }
    public double getPercentFromOffset(int offset){
        if (offset < 0)
            offset = 0;
        double percent = 100 * (double) offset / reader.getTextLength();

        // Due to rounding percent may be more than 100.
        return Math.min(100.0, percent);
    }
}



