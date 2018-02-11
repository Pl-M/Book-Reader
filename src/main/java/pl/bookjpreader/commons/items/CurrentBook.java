/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons.items;

import pl.bookjpreader.booksfactory.BookEntity;
import pl.bookjpreader.booksfactory.BookHandler;

/**
 * This class is a container for a currently open book.
 * All actions with this book can be done through this class.
 */
public class CurrentBook implements RegistryElement {
    /**
     * Currently opened book which is shown.
     */
    private BookHandler currentBookHandler = null;

    public void setNewBook(BookHandler currentBook){
        this.currentBookHandler = currentBook;
    }

    public double getBookPosition(){
        if (currentBookHandler == null) {
            return 0.0;
        }
        return currentBookHandler.getEntity().getPosition();
    }
    public void setBookPosition(double newPos){
        if (currentBookHandler == null)
            return;
        currentBookHandler.getEntity().setPosition(newPos);
    }
    public Double searchText(String pattern) {
        Double result = null;
        if (currentBookHandler != null) {
            result = currentBookHandler.searchText(pattern, getBookPosition());
        }
        return result;
    }

    public BookEntity getCurrentBook(){
        if (currentBookHandler == null){
            return null;
        }
        return currentBookHandler.getEntity();
    }
    public BookHandler getCurrentBookHandler(){
       return currentBookHandler;
    }

}
