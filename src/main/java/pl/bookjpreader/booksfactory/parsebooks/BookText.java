/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory.parsebooks;

public interface BookText {
    public String getText(String encoding) throws Exception;
    /*
     * @return: all text in the book.
     */

    // public String getInfo() throws Exception;
    /*
     * @return: information about a book: filename, file size, title, etc.
     */
}
