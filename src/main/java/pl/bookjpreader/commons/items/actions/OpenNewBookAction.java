/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */

package pl.bookjpreader.commons.items.actions;


import pl.bookjpreader.booksfactory.BookEntity;
import pl.bookjpreader.booksfactory.BookHandler;
import pl.bookjpreader.commons.ErrorHandler;
import pl.bookjpreader.commons.ProgramRegistry;
import pl.bookjpreader.commons.items.BookShelf;
import pl.bookjpreader.commons.items.CurrentBook;

public class OpenNewBookAction extends BaseValueAction<BookEntity, BookHandler> {

    /**
     * This function set given book as a current.
     * It also checks whether the book actually exists on disc and can be opened.
     * By opening a new book it also takes all parameters of this book (like encoding and position),
     * if this book is already on the shelf it will be changed to the new one.
     */
    @Override
    BookHandler execute(BookEntity entity){
        // Check the book.
        BookHandler handler = null;
        try{
            handler = new BookHandler(entity);
        } catch (java.nio.charset.MalformedInputException e){
            ErrorHandler.handle("Encoding error", e);
        } catch (Exception e) {
            ErrorHandler.handle("An error occurred while opening the file", e);
        }

        if (handler != null) {
            ProgramRegistry.INSTANCE.getForClass(CurrentBook.class)
                    .setNewBook(handler);
            ProgramRegistry.INSTANCE.getForClass(BookShelf.class)
                    .addBookAsNew(handler.getEntity());
        }
        return handler;
    }
}
