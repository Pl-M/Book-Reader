/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory.parsebooks;

import java.nio.file.Path;


public class FilesFactory {

    public static BookText createBookObj(Path filePath){
        /*
         * Check for file type and return corresponding object.
         * Currently works only with txt files.
         */
        if (compareExt("fb2", filePath))
            return new FB2File(filePath);
        else if (compareExt("html", filePath))
            return new HTMLFile(filePath);
        else if (compareExt("htm", filePath))
            return new HTMLFile(filePath);
        else
            return new TxtFile(filePath);
    }
    private static boolean compareExt(String ext, Path filePath){
        /*
         * @param ext: given extention;
         * @param filePath: path of the file which extention should be
         * compared with ext;
         * @return: true if ext equals to extention of the file.
         */
        String fileExt = null;
        String fileName = filePath.getFileName().toString();

        int start = fileName.lastIndexOf('.');
        if (start == -1) // no extention found
            fileExt = "";
        else
            fileExt = fileName.substring(start+1).toLowerCase();

        if (ext.toLowerCase().equals(fileExt))
            return true;
        return false;
    }
}
