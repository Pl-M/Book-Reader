/*
 * Copyright (c) 2016-2018 Pavel_M-v.
 */
package pl.bookjpreader.booksfactory.parsers;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class parses fb2 files.
 */
class FB2File implements BookParser {

    @Override
    public Set<String> getFileTypes() {
        return Collections.singleton("fb2");
    }

    /**
     * This function is used standard Java methods to parse fb2 file
     * (which is xml) and output it as string.
     * It takes into account only body tags since they contain book text.
     * @param encoding doesn't use encoding - it is taken by parser from xml file.
     */
    @Override
    public String getText(final Path filePath, final String encoding) throws Exception{

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;

        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(filePath.toFile());
        }
        catch(ParserConfigurationException | SAXException | IOException e) {
            throw e;
        }

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("body");

        StringBuilder sb = new StringBuilder();
        if (nList != null && nList.getLength() > 0)
            readNodes(sb, nList);

        return sb.toString();
    }

    /**
     * This function goes through nodes in recursion and
     * if need be adds additional formatting to some elements.
     * @param sb
     * @param nList list of nodes.
     * This function is introduced instead of getTextContent since it
     */
    private void readNodes(final StringBuilder sb, NodeList nList){
        String spaces = new String(new char[6]).replace("\0", "\u00A0");

        for (int i = 0 ; i < nList.getLength();i++) {
            if (nList.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element el = (Element)nList.item(i);
            String tagName = el.getNodeName();

            if (tagName.equals("p")){
                /* Put non-breaking spaces before each paragraph
                 * and add newline after.
                 */
                sb.append(spaces);
                sb.append(el.getTextContent());
                sb.append("\n");
            }
            else if (tagName.equals("title")){
                sb.append("\n\n");
                sb.append(el.getTextContent());
                sb.append("\n\n");
            }
            else if (tagName.equals("section")){
                sb.append("\n\n");
                readNodes(sb, el.getChildNodes());
                sb.append("\n\n");
            }
            else if (el.hasChildNodes())
                readNodes(sb, el.getChildNodes());
            else
                sb.append(el.getTextContent());
        }
    }
}
