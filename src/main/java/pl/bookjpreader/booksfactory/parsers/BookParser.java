/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory.parsers;

import java.nio.file.Path;
import java.util.Set;

public interface BookParser {
    /**
     * @return all text in the book.
     */
    String getText(Path filePath, String encoding) throws Exception;

    Set<String> getFileTypes();
}

