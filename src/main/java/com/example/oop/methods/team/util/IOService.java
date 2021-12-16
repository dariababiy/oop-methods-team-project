package com.example.oop.methods.team.util;

import com.example.oop.methods.team.model.Author;
import com.example.oop.methods.team.model.Book;
import com.example.oop.methods.team.model.Library;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * INPUT / OUTPUT SERVICE
 */
public class IOService {

    public static Library loadFromXml(String filePath) {
        try {
            /**
             * STEP 0: PREPARE OBJECTS
             */
            Library library = new Library();
            String schemaPath = "src/main/resources/xml/input-schema.xsd";
            Schema s = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new File(schemaPath));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setSchema(s);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File(filePath));
            Element root = document.getDocumentElement();
            /**
             * STEP 1: LOAD AUTHORS
             */
            NodeList authors = root.getElementsByTagName("author");
            for (int i = 0; i < authors.getLength(); i++) {
                if (authors.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element xmlAuthor = (Element) authors.item(i);
                    Author author = new Author();
                    author.setId(Long.parseLong(xmlAuthor.getAttribute("id")));
                    author.setFullName(xmlAuthor.getAttribute("fullname"));
                    author.setBirthDay(xmlAuthor.getAttribute("date-of-birth"));
                    author.setLang(xmlAuthor.getAttribute("lang"));
                    author.setCountry(xmlAuthor.getAttribute("country"));
                    library.getAuthors().add(author);
                }
            }
            /**
             * STEP 2: LOAD BOOKS
             */
            NodeList books = root.getElementsByTagName("book");
            for (int i = 0; i < books.getLength(); i++) {
                if (books.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element xmlBook = (Element) books.item(i);
                    Book book = new Book();
                    book.setId(Long.parseLong(xmlBook.getAttribute("id")));
                    book.setTitle(xmlBook.getAttribute("title"));
                    book.setGenre(xmlBook.getAttribute("genre"));
                    book.setYear(Integer.parseInt(xmlBook.getAttribute("year")));
                    Long authorId =
                            Long.parseLong(
                                    ((Element) xmlBook.getParentNode())
                                            .getAttribute("id")
                            );
                    Author author = library.getAuthors().stream()
                            .filter(a -> a.getId().equals(authorId))
                            .findFirst()
                            .orElseThrow(() -> new Exception("Author with id " + authorId + "not found"));
                    book.setAuthor(author);
                    library.getBooks().add(book);
                }
            }
            return library;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeToXml(Library library, String fileName) {
        try {
            List<Author> authors = library.getAuthors();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("library");
            doc.appendChild(root);

            for (Author author: authors) {
                Element xmlAuthor = doc.createElement("author");
                xmlAuthor.setAttribute("id", author.getId().toString());
                xmlAuthor.setAttribute("fullname", author.getFullName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String birthDay = sdf.format(author.getBirthDay());
                xmlAuthor.setAttribute("date-of-birth", birthDay);
                xmlAuthor.setAttribute("lang", author.getLang());
                xmlAuthor.setAttribute("country", author.getCountry());
                root.appendChild(xmlAuthor);
                List<Book> books = library.getBooks().stream()
                        .filter(book -> book.getAuthor().getId().equals(author.getId()))
                        .collect(Collectors.toList());
                for (Book book: books) {
                    Element xmlBook = doc.createElement("book");
                    xmlBook.setAttribute("id", book.getId().toString());
                    xmlBook.setAttribute("title", book.getTitle());
                    xmlBook.setAttribute("genre", book.getGenre());
                    xmlBook.setAttribute("year", book.getYear().toString());
                    xmlAuthor.appendChild(xmlBook);
                }
            }

            String dstFilePath = String.format("src/main/resources/xml/output/%s_%d.xml", fileName, System.currentTimeMillis());
            File dstFile = new File(dstFilePath);
            dstFile.createNewFile();

            Source domSource = new DOMSource(doc);
            Result fileResult = new StreamResult(dstFile);
            Transformer t = TransformerFactory.newDefaultInstance().newTransformer();
            t.setOutputProperty("encoding", "utf-8");
            t.transform(domSource, fileResult);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
