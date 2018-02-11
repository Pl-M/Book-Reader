/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.commons.items;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import pl.bookjpreader.booksfactory.BookEntity;
import pl.bookjpreader.booksfactory.BookHandler;
import pl.bookjpreader.commons.ProgramRegistry;

/**
 * This class is a container for all books.
 * It is used to keep track of all opened books.
 */
public class BookShelf implements RegistryElement {
    /**
     * Keep track of all books with their positions and encodings.
     */
    final private Set<BookEntity> books = new HashSet<>();

    public Set<BookEntity> getBooks(){
        return new HashSet<>(this.books);
    }
    /**
     * This function doesn't check whether a book exists on disc or not.
     * It is done so to not lose information about old books if they were
     * e.g. on removable drive.
     * @param bookEntity a new book, if identical book is in the shelf the old
     * one will be removed.
     */
    public void addBookAsNew(BookEntity bookEntity){
        removePreviousVersion(bookEntity);
        books.add(bookEntity);
    }
    /**
     * @param bookEntity a book to remove, also check current book.
     */
    public void removePreviousVersion(BookEntity bookEntity){
        if (books.contains(bookEntity)){
            books.remove(bookEntity);

            // Check current book and current position.
            final BookHandler current = ProgramRegistry.INSTANCE
                    .getForClass(CurrentBook.class).getCurrentBookHandler();
            if (current != null && current.getEntity().equals(bookEntity)){
                // TODO close book action.
                //currentBook.set(null);
            }
        }
    }
    /**
     * @param filePath look for a book having given filePath;
     * @return an existing book if available, otherwise {@code null}.
     */
    public BookEntity getBook(Path filePath){
        for (BookEntity book: books){
            if (book.getFilePath().equals(filePath))
                return book;
        }
        return null;
    }

}
