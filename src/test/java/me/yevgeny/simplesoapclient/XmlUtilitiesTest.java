package me.yevgeny.simplesoapclient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlUtilitiesTest {

    static private String testXml;

    @BeforeAll
    static void setUp() throws IOException {
        testXml = new String(Files.readAllBytes(Paths.get("src/test/resources/xmlExample.xml")));
    }

    @Test
    void xmlStringToDocument() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        NodeList actual = document.getElementsByTagName("age");
        assertEquals("3", actual.item(0).getTextContent());
    }

    @Test
    void xmlDocumentToStringDoNotOmitXmlDeclaration()
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.xmlDocumentToString(document, false);
        assertEquals(testXml, actual);
    }

    @Test
    void xmlDocumentToStringOmitXmlDeclaration()
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.xmlDocumentToString(document, true);
        assertEquals(testXml.substring(testXml.indexOf('\n') + 1), actual);
    }

    @Test
    void getTextContentOfXmlElementByFullXpath()
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException,
                   XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.getTextContentOfXmlElement(document, "//zoo/duck/age");
        assertEquals("3", actual);
    }

    @Test
    void getTextContentOfXmlElementByTagName()
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException,
                   XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.getTextContentOfXmlElement(document, "age");
        assertEquals("3", actual);
    }

    @Test
    void findXmlNodeByXPath() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException,
                                     XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        Node actual = XmlUtilities.findXmlNodeByXPath(document, "//zoo/duck/age");
        assertEquals("3", actual.getTextContent());
    }

    @Test
    void findXmlNodeByName() throws IOException, SAXException, ParserConfigurationException, XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        Node actual = XmlUtilities.findXmlNodeByName(document.getChildNodes(), "age");
        assertEquals("3", actual.getTextContent());
    }
}