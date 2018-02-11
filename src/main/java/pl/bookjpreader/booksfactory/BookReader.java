/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.booksfactory;

import pl.bookjpreader.booksfactory.parsers.FileParserResolver;

/**
 * This class is used to read book from the disc and work with
 * its text.
 */
final class BookReader {
    final private int textLength;
    final private String text;

    /**
     * @param bookEntity book to read.
     */
    BookReader(BookEntity bookEntity) throws Exception {
        text = FileParserResolver.getText(
                bookEntity.getFilePath(), bookEntity.getEncoding());
        textLength = text.length();
    }
    /**
     * This constructor may be used if text was get elsewhere,
     * and for tests.
     * @param text text of the book.
     */
    BookReader(String text){
        this.text = text;
        textLength = text.length();
    }

    int getTextLength() {
        return textLength;
    }
    String getText(){
        return text;
    }
}
