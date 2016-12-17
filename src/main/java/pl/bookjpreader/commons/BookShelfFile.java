/*******************************************************************************
 * Copyright (c) 2016 Pavel_M-v.
 *
 *******************************************************************************/
package pl.bookjpreader.commons;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.bookjpreader.booksfactory.BookFile;

/*
 * This class saves/loads books and their properties to a xml file.
 */
public class BookShelfFile {

    private final BookShelf bookShelf;
    private final Path filePath;

    private BookShelfFile(BookShelf bookShelf, Path filePath) {
        this.filePath = filePath;
        this.bookShelf = bookShelf;
    }

    public static void load(BookShelf bookShelf, Path filePath){
        /*
         * @param bookShelf: books and their properties to save/load; this class
         * doesn't return a new value it changes bookShelf;
         * @param filePath: where to save/ from where to load options.
         */
        BookShelfFile file = new BookShelfFile(bookShelf, filePath);
        file.load();
    }
    public static void save(BookShelf bookShelf, Path filePath){
        BookShelfFile file = new BookShelfFile(bookShelf, filePath);
        file.save();
    }

    private void load(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;

        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(filePath.toString());
        }
        catch(ParserConfigurationException pce) {
            System.out.println("BookShelf File: ParserConfigurationException");
            return;
        }
        catch(SAXException se) {
            System.out.println("BookShelf File: SAXException");
            return;
        }
        catch(IOException ioe) {
            System.out.println("BookShelf File: IOException");
            return;
        }

        doc.getDocumentElement().normalize();

        // Add all books to bookshelf.
        NodeList nList = doc.getElementsByTagName(Headers.BOOK.toString());

        if (nList != null && nList.getLength() > 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element)nList.item(i);
                Path filePath = Paths.get(el.getAttribute(Headers.FILENAME.toString()));
                BookFile newBook;

                // If Encoding tag is present.
                if (el.getElementsByTagName(Headers.ENCODING.toString()).getLength() != 0){
                    String encoding = el.getElementsByTagName(
                        Headers.ENCODING.toString()).item(0).getTextContent();
                    newBook = new BookFile(filePath, encoding);
                }
                else
                    newBook = new BookFile(filePath);

                // If Position tag is present.
                if (el.getElementsByTagName(Headers.POSITION.toString()).getLength() != 0){
                    double pos = Double.parseDouble(el.getElementsByTagName(
                            Headers.POSITION.toString()).item(0).getTextContent());
                    newBook.setPosition(pos);
                }

                bookShelf.addReplaceBook(newBook);
            }
        }

        // Open current book. Should be after addition of all other books to
        // read position and other options if need be.
        nList = doc.getElementsByTagName(Headers.CURRENT_BOOK.toString());
        if (nList != null && nList.getLength() > 0){
            Element el = (Element)nList.item(0);
            Path filePath = Paths.get(el.getAttribute(Headers.FILENAME.toString()));
            bookShelf.openNewBook(bookShelf.getBook(filePath));
        }

    }
    private void save(){
        /*
         * Save books to the file.
         */
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        Document doc;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("BookShelf");
            doc.appendChild(mainRootElement);

            // Append child elements to root element.
            Element bookEl;
            // Add current book.
            if (bookShelf.currentBook.get() != null){
                bookEl = doc.createElement(Headers.CURRENT_BOOK.toString());
                bookEl.setAttribute(Headers.FILENAME.toString(),
                        bookShelf.currentBook.get().filepath.toString());
                mainRootElement.appendChild(bookEl);
            }

            // Add all other books.
            for (BookFile book : bookShelf.getBooks())
                mainRootElement.appendChild(createBookElement(doc, book));

            // Use a Transformer to save as a xml file.
            Transformer tr = TransformerFactory.newInstance().newTransformer();

            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            tr.transform(new DOMSource(doc),
                    new StreamResult(new FileOutputStream(filePath.toString())));
        }
        catch (TransformerException te){
            System.out.println(te.getMessage());
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element createBookElement(Document doc, BookFile book){
        Element bookEl = doc.createElement(Headers.BOOK.toString());
        bookEl.setAttribute(Headers.FILENAME.toString(), book.filepath.toString());

        Element pos = doc.createElement(Headers.POSITION.toString());
        pos.appendChild(doc.createTextNode(String.valueOf(book.getPosition())));
        bookEl.appendChild(pos);

        Element encoding = doc.createElement(Headers.ENCODING.toString());
        encoding.appendChild(doc.createTextNode(String.valueOf(book.getEncoding())));
        bookEl.appendChild(encoding);

        return bookEl;
    }
    enum Headers {
        /*
         * This values corresponds to tags/attributes in xml file.
         */
        CURRENT_BOOK("CurrentBook"),
        BOOK("Book"),
        POSITION("Position"),
        FILENAME("Filename"),
        ENCODING("Encoding");
        private final String name;

        Headers(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
}

