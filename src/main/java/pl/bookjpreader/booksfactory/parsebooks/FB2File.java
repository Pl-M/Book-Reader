/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.booksfactory.parsebooks;
/*
 * This class parses fb2 files.
 */

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FB2File implements BookText{
    final private Path filePath;
    final private StringBuilder sb;

    public FB2File(Path filePath) {
        this.filePath = filePath;
        sb = new StringBuilder();
    }

    @Override
    public String getText(String encoding) throws Exception{
        /* This function is used standard Java methods to parse fb2 file (which is xml)
         * and output it as string.
         * It takes into account only body tags since they contain book text.
         * @param encoding: doesn't use encoding - it is taken by parser from xml file.
         */

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;

        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(filePath.toFile());
        }
        catch(ParserConfigurationException pce) {
            throw pce;
        }
        catch(SAXException se) {
            throw se;
        }
        catch(IOException ioe) {
            throw ioe;
        }

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("body");

        if (nList != null && nList.getLength() > 0)
            readNodes(nList);

        return sb.toString();
    }

    private void readNodes(NodeList nList){
        /*
         * This function in recursion goes through nodes and
         * if need be add additional formatting to some elements.
         * @param nList: list of nodes.
         * This function is introduced instead getTextContent since it
         * adds its own formatting.
         */
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
                readNodes(el.getChildNodes());
                sb.append("\n\n");
            }
            else if (el.hasChildNodes())
                readNodes(el.getChildNodes());
            else
                sb.append(el.getTextContent());
        }
    }
}
