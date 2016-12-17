/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory.parsebooks;
/*
 * This class parses HTML files using the external
 * Jsoup library (https://jsoup.org).
 */

import java.io.File;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLFile implements BookText{
    final private Path filePath;

    public HTMLFile(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getText(String encoding) throws Exception{
        /* This function is using jsoup library to parse html file
         * and output it as a string;
         * @param encoding: doesn't use encoding - it is taken by parser from html file.
         */
        Document doc = Jsoup.parse(new File(filePath.toUri()), null);

        //Make html to not reformat output (to preserve linebreaks and spacing).
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));

        //Add newlines into/after/near html tags like <h1>, <br>, <p>, etc.
        doc.select("h1,h2,h3,h4,h5,h6").after("-newline-");
        doc.select("br").append("-newline-");
        doc.select("p").prepend("-newline-");

        return doc.text().replaceAll("-newline-", "\n");
    }

}
