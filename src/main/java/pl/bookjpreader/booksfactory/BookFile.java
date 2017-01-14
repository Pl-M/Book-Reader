/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory;

import java.nio.file.Path;

/*
 * This class contains all information about a current book such as
 * its encoding, length, path, etc.
 * It contains methods (due to Reader class) to read files.
 *
 */
public class BookFile{
    /*
     * @param reader: don't initialize in constructor to prevent
     * reading all files in the shelf.
     * @param bookPosition: a percent which indicates where cursor is
     * positioned now;
     * @param encoding: encoding of text in file, by default it is UTF-8, some
     * parsers detect encoding on their own and don't use this parameter.
     */
    final public Path filepath;
    final private String encoding;

    private double bookPosition;
    private Reader reader;

    public BookFile(Path filepath){
        /*
         * @param filePath: path of the file;
         */
        this.filepath = filepath;
        reader = null;
        encoding = "UTF-8";
    }
    public BookFile(Path filepath, String encoding){
        /*
         * @param filePath: path of the file;
         * @param encoding: encoding of the file, currently it is only needed
         * for plain text files for other files it is ignored.
         */
        this.filepath = filepath;
        reader = null;
        this.encoding = encoding;
    }
    @Override
    public boolean equals(Object bookFile){
        /* Two objects considered to be equal if their path are equal.
         */
        if (bookFile == null)
            return false;
        return ((BookFile)bookFile).filepath.equals(this.filepath);
    }
    @Override
    public int hashCode( ){
        return this.filepath.hashCode();
    }
    public String getFileName(){
        return filepath.getFileName().toString();
    }
    public double getPosition(){
        return bookPosition;
    }
    public void setPosition(double newPos){
        if (newPos < 0)
            newPos = 0;
        else if (newPos > 100)
            newPos = 100;
        bookPosition = newPos;
    }
    public String getEncoding(){
        return encoding;
    }

    public Reader getReader() throws Exception{
        /*
         * @return: reader if it is already initialized or
         * initialize it. Throws error if reader can't be initialized:
         * file is not available, etc.
         */
        if (reader != null)
            return reader;

        reader = new Reader(filepath, encoding);
        return reader;
    }
    public static class Reader {
        /*
         * @param maxParagraphLength: if paragraph exceeds this limit it will be split.
         */
        final public int textLength;
        final private String fileFullText;
        final private int maxParagraphLength = 500;

        public Reader(Path filepath, String encoding) throws Exception{
            /*
             * @param encoding: encoding of text, need this to
             * encode text files;
             * @param filepath: path of the file to read.
             */
            fileFullText = FilesFactory.createBookObj(filepath).getText(encoding);
            textLength = fileFullText.length();
        }

        public Reader(String text){
            /* This constructor may be used if text were get elsewhere,
             * and also for tests.
             */
            fileFullText = text;
            textLength = fileFullText.length();
        }

        public String getNextBlock(int startPos){
            return getNextBlock(startPos, 100);
        }
        public String getPreviousBlock(int endPos){
            return getPreviousBlock(endPos, 100);
        }

        public String getNextBlock(int startPosition, int length){
            /*
             * @param length: block should be bigger than length (this is to eliminate
             * blocks consisting only from one newline).
             * @param startPosition: position in the text which is the beginning of the block.
             * @return: block of text beginning from the position pos and ending at newline (or
             * at 800th symbol from the beginning if no newline found).
             */

            final int pos = startPosition;
            if (pos < 0 || pos >= textLength)
                return null;

            if (pos >= textLength - length)
                return fileFullText.substring(pos, textLength);

            int end = fileFullText.indexOf("\n", pos + length);
            if (end == -1)
                end = textLength;

            int maxEnd = pos + length + maxParagraphLength;
            if (end > maxEnd){ // paragraph is too long
                // Search the nearest space.
                end = fileFullText.indexOf(" ", maxEnd);
                if (end == -1 || (end - maxEnd) > 100)
                    end = maxEnd;
            }

            // Include newline to the end.
            return fileFullText.substring(pos, end + 1);
        }
        public String getPreviousBlock(int endPosition, int length){
            /*
             * @param length: block should be bigger than length (this is to eliminate
             * blocks consisting only from one newline).
             * @param endPosition: position in the text which is the end of the block.
             * @return: block of text beginning after newline (or
             * at 800th symbol from the end if no newline found) and ending in pos.
             */

            final int newPos = endPosition;

            if (newPos <= 0 || newPos > textLength)
                return null;

            if (newPos <= length)
                return fileFullText.substring(0, newPos);

            int begin = fileFullText.lastIndexOf("\n", newPos - length);

            if (begin == -1)
                begin = 0;
            else
                begin += 1; // exclude newline from the beginning

            int minBegin = newPos - length - maxParagraphLength;
            if (begin < minBegin){ // paragraph is too long
                // Search the nearest space.
                begin = fileFullText.lastIndexOf(" ", minBegin);
                if (begin == -1 || begin < minBegin - 100)
                    begin = minBegin;
            }

            return fileFullText.substring(begin, newPos);
        }

        /*
         * Functions to convert from percent to position and back.
         */
        public int getOffsetFromPercent(double percent){
            if (percent < 0)
                percent = 0;

            int offset = (int)Math.round(textLength * percent / 100);

            // Due to rounding percent may be more than 100.
            return Math.min(offset, textLength);
        }
        public double getPercentFromOffset(int offset){
            if (offset < 0)
                offset = 0;

            // Due to rounding percent may be more than 100.
            return Math.min(100.0, (double) offset / (textLength / 100));
        }
    }
}
