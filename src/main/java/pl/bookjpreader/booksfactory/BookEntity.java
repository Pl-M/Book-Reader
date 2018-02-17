/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory;

import java.nio.file.Path;

/**
 * This class contains a book entity: all information about the book such as
 * its encoding, length, path, etc. It contains only data.
 *
 */
public class BookEntity{
    final private static String DEFAULT_ENCODING = "UTF-8";

    final private Path filePath;
    final private String encoding;

    /**
     * A percent which indicates where cursor is positioned now;
     */
    private double bookPosition;

    public BookEntity(Path filePath){
        this.filePath = filePath;
        encoding = DEFAULT_ENCODING;
    }
    /**
     * @param filePath: path of the file;
     * @param encoding: encoding of the file, currently it is only needed
     * for plain text files for other files it is ignored.
     */
    public BookEntity(Path filePath, String encoding){
        this.filePath = filePath;
        if (encoding == null) {
            this.encoding = DEFAULT_ENCODING;
        }
        else {
            this.encoding = encoding;
        }
    }
    /**
     *  Two objects considered to be equal if their paths are equal.
     */
    @Override
    public boolean equals(Object obj){

        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BookEntity bookFile = (BookEntity)obj;
        return bookFile.getFilePath().equals(this.getFilePath());
    }
    @Override
    public int hashCode( ){
        return this.filePath.hashCode();
    }
    public String getFileName(){
        return filePath.getFileName().toString();
    }
    public Path getFilePath() {
        return filePath;
    }
    public double getPosition(){
        return bookPosition;
    }
    public void setPosition(double newPos){
        if (newPos < 0) {
            newPos = 0;
        } else if (newPos > 100) {
            newPos = 100;
        }
        bookPosition = newPos;
    }
    public String getEncoding(){
        return encoding;
    }

}
