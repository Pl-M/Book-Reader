/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory.parsebooks;
/*
 * This class parses simple text files,
 * currently only using unicode encoding.
 */

import java.nio.file.Files;
import java.nio.file.Path;

public class TxtFile implements BookText{
    final private Path filePath;

    public TxtFile(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getText(String encoding) throws Exception{
        /*
         * @param encoding: text encoding.
         * There will be no error if encoding is wrong.
         * Formatting isn't changed since it is considered that simple text
         * is already formatted.
         */

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
