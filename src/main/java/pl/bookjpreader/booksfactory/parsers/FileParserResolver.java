/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory.parsers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public final class FileParserResolver {
    private static final Map<String, BookParser> PARSERS = new HashMap<>();
    private static final BookParser DEFAULT_PARSER = new TxtFile();
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024 ; // 10 MB

    static {
        Set<BookParser> bookTexts = new HashSet<>();
        bookTexts.add(DEFAULT_PARSER);
        bookTexts.add(new FB2File());
        bookTexts.add(new HTMLFile());

        bookTexts.forEach(parser ->
            parser.getFileTypes().forEach(type -> PARSERS.put(type, parser)));
    }

    public static String getText(final Path filePath, String encoding) throws Exception {
        if (Files.size(filePath) > MAX_FILE_SIZE) {
            throw new Exception("File size exceeds allowed value.");
        }
        return getBookParser(filePath).getText(filePath, encoding);
    }

    public static BookParser getBookParser(final Path filePath) {
        final String ext = getExt(filePath);
        return PARSERS.getOrDefault(ext, DEFAULT_PARSER);
    }
    /**
     * @param filePath: path of the file to get its extension;
     * @return extension of the file.
     */
    private static String getExt(Path filePath){
        String fileExt;
        String fileName = filePath.getFileName().toString();

        int start = fileName.lastIndexOf('.');
        if (start == -1) // no extension found
            fileExt = null;
        else
            fileExt = fileName.substring(start+1).toLowerCase();

        return fileExt;
    }
}
