/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory.parsers;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * This class parses simple text files,
 * currently only using unicode encoding.
 */
class TxtFile implements BookParser {


    @Override
    public Set<String> getFileTypes() {
        return Collections.singleton("txt");
    }
    /**
     * @param encoding text encoding, there will be no error if encoding is wrong.
     * Formatting isn't changed since it is considered that simple text
     * is already formatted.
     */
    @Override
    public String getText(final Path filePath, final String encoding) throws Exception{

        /*
        List<String> fileText = new ArrayList<>();

        Charset charset = Charset.forName(encoding);
        fileText = Files.readAllLines(filePath, charset);
        // Add non-breaking spaces to indicate paragraphs.
        String add = "\n" + new String(new char[6]).replace("\0", "\u00A0");
        return String.join(add, fileText);
        */

        byte [] b = Files.readAllBytes(filePath);

        return new String(b, encoding).replaceAll("\\r\\n", "\n");
    }

}
