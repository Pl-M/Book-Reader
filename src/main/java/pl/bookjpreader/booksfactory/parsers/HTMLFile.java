/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory.parsers;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class parses HTML files using the external
 * Jsoup library (https://jsoup.org).
 */
class HTMLFile implements BookParser {

    @Override
    public Set<String> getFileTypes() {
        return new HashSet<>(Arrays.asList("html", "htm"));
    }

    /**
     * This function is using jsoup library to parse html file
     * and output it as a string;
     * @param encoding: doesn't use encoding - it is taken by parser from html file.
     */
    @Override
    public String getText(final Path filePath, final String encoding) throws Exception{

        Document doc = Jsoup.parse(new File(filePath.toUri()), null);

        //Make html to not reformat output (to preserve line breaks and spacing).
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));

        //Add newlines into/after/near html tags like <h1>, <br>, <p>, etc.
        doc.select("h1,h2,h3,h4,h5,h6").after("-newline-");
        doc.select("br").append("-newline-");
        doc.select("p").prepend("-newline-");

        return doc.text().replaceAll("-newline-", "\n");
    }

}
