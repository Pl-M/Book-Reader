/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.bookjpreader.booksfactory.BookFile;

/*
 * This class is a container for all books.
 * It is used to keep track of all opened books.
 * Also it has more convenient interface for current book and its position.
 */

public class BookShelf {
    /*
     * @param currentBook: currently opened book which is shown; don't change
     * this parameter outside this class, use openNewBook function instead,
     * can contain NULL;
     * @param currentBookPosition: current position of the opened book; don't change
     * this parameter outside this class, use setPosition functions instead,
     * can contain NULL.
     * @param books: keep track of all books with their positions and encodings.
     * @param stopAnimation: listen to this property to stop animation
     * in the current book (e.g. scrolling), is useful to stop animation when a new
     * dialog appear; don't change this variable to stop animation, use
     * stopAnimation() instead.
     */

    final private Set<BookFile> books;
    final public ObjectProperty<BookFile> currentBook;
    final public ObjectProperty<Double> currentBookPosition;
    final public BooleanProperty stopAnimation;

    public BookShelf() {
        books = new HashSet<>();
        currentBook = new SimpleObjectProperty<>();
        currentBookPosition = new SimpleObjectProperty<>();
        stopAnimation = new SimpleBooleanProperty(false);

        // Change position in the currently opened book.
        currentBookPosition.addListener((obs, oldPos, newPos) ->{
            if (currentBook.get() != null && newPos != null)
                currentBook.get().setPosition(newPos);
        });
    }
    public boolean openNewBook(BookFile bookFile){
        /*
         * This function set given book as a current.
         * It also checks whether the book actually exists on disc and can be opened.
         * By opening a new book it also takes all parameters of this book (like encoding and position),
         * if this book is already on the shelf it will be changed to the new one.
         * @param bookFile: a book to open.
         * @return: true if book is available, else false.
         */

        // Check the book.
        try{
            bookFile.getReader();
        }
        catch (java.nio.charset.MalformedInputException je){
            System.out.println("Encoding error.");
            return false;
        }
        catch (Exception e) {
            System.out.println("An error occured while opening the file.");
            e.printStackTrace();
            return false;
        }
        addReplaceBook(bookFile);

        currentBook.set(bookFile);
        currentBookPosition.set(currentBook.get().getPosition());
        return true;
    }
    public void setOffsetPosition(int newPos){
        if (currentBook.get() == null)
            return;

        double percent;
        try{
            percent = currentBook.get().getReader().getPercentFromOffset(newPos);
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        currentBookPosition.set(percent);
    }
    public void setPercentPosition(double newPos){
        if (currentBook.get() == null)
            return;
        currentBookPosition.set(newPos);
    }
    public Set<BookFile> getBooks(){
        return new HashSet<>(this.books);
    }
    public void addReplaceBook(BookFile bookFile){
        /*
         * This function doesn't check whether a book exists on disc or not.
         * It is done so to not lose information about old books if they were
         * e.g. on removable drive.
         * @param bookFile: a new book, if identical book is in the shelf the old
         * one will be removed.
         */
        removeBook(bookFile);
        books.add(bookFile);
    }
    public void stopAnimation(){
        /*
         * Stop animation in the current book (if available).
         */
        stopAnimation.set(!stopAnimation.get());
    }

    public void removeBook(BookFile bookFile){
        /*
         * @param bookFile: a book to remove, also check current book.
         */
        if (books.contains(bookFile)){
            books.remove(bookFile);

            // Check current book and current position.
            if (currentBook.get() != null && currentBook.get().equals(bookFile)){
                currentBook.set(null);
                currentBookPosition.set(null);
            }
        }
    }
    public BookFile getBook(Path filePath){
        /*
         * @param filePath: look for a book having given filePath;
         * @return: an existing book if available, otherwise null.
         */
        for (BookFile book: books){
            if (book.filepath.equals(filePath))
                return book;
        }
        return null;
    }
    public String getCurrentBookFileName(){
        if (currentBook.get() != null)
            return currentBook.get().getFileName();
        else
            return "";
    }
    public String getCurrentBookFilePath(){
        if (currentBook.get() != null)
            return currentBook.get().filepath.toString();
        else
            return "";
    }


}
