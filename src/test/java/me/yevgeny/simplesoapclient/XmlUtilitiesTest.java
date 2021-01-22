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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlUtilitiesTest {

    static private String testXml;
    static private String testXmlWithDeclaration;

    @BeforeAll
    static void setUp() throws IOException {
        testXml = new String(Files.readAllBytes(Paths.get("src/test/resources/xmlExample.xml")));
        testXmlWithDeclaration =
                new String(Files.readAllBytes(Paths.get("src/test/resources/xmlExampleWithXmlDeclaration.xml")));
    }

    @Test
    void xmlStringToDocument() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        NodeList actual = document.getElementsByTagName("age");
        assertEquals("3", actual.item(0).getTextContent());
    }

    @Test
    void xmlDocumentToStringDoNotOmitXmlDeclaration() throws IOException, SAXException, ParserConfigurationException,
            TransformerException {
        Document document = XmlUtilities.xmlStringToDocument(testXmlWithDeclaration);
        String actual =
                XmlUtilities.xmlDocumentToString(document, false).replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        assertEquals(testXmlWithDeclaration, actual);
    }

    @Test
    void xmlDocumentToStringOmitXmlDeclaration() throws IOException, SAXException, ParserConfigurationException,
            TransformerException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual =
                XmlUtilities.xmlDocumentToString(document, true).replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        assertEquals(testXml, actual);
    }

    @Test
    void getTextContentOfXmlElementByFullXpath() throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.getTextContentOfXmlElement(document, "//zoo/duck/age");
        assertEquals("3", actual);
    }

    @Test
    void getTextContentOfXmlElementByTagName() throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        String actual = XmlUtilities.getTextContentOfXmlElement(document, "age");
        assertEquals("3", actual);
    }

    @Test
    void getTextContentOfXmlElementNegativeEmptyPath() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.getTextContentOfXmlElement(document, ""));
        assertEquals("Path to field parameter was not set correctly", xmlParsingException.getMessage());
    }

    @Test
    void getTextContentOfXmlElementNegativeNullPath() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.getTextContentOfXmlElement(document, null));
        assertEquals("Path to field parameter was not set correctly", xmlParsingException.getMessage());
    }

    @Test
    void getTextContentOfXmlElementNegativeInvalidPath() throws IOException, SAXException,
            ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.getTextContentOfXmlElement(document, "\\\\"));
        assertEquals("Couldn't find node named \"\\\\\"", xmlParsingException.getMessage());
    }

    @Test
    void getTextContentOfXmlElementNegativeInvalidElement() throws IOException, SAXException,
            ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.getTextContentOfXmlElement(document, "//zoo/.."));
        assertEquals("No text content was found at \"//zoo/..\"", xmlParsingException.getMessage());
    }

    @Test
    void getTextContentOfXmlElementNegativeNoTextContentAtPath() throws IOException, SAXException,
            ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.getTextContentOfXmlElement(document, "size"));
        assertEquals("No text content was found at \"size\"", xmlParsingException.getMessage());
    }

    @Test
    void findXmlNodeByXPath() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException,
            XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        Node actual = XmlUtilities.findXmlNodeByXPath(document, "//zoo/duck/age");
        assertEquals("3", actual.getTextContent());
    }

    @Test
    void findXmlNodeByXPathNegativeNoNodeAtPath() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.findXmlNodeByXPath(document, "//zoo/duck/gender"));
        assertEquals("Couldn't find node in the following path: \"//zoo/duck/gender\"",
                xmlParsingException.getMessage());
    }

    @Test
    void findXmlNodeByName() throws IOException, SAXException, ParserConfigurationException, XmlParsingException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        Node actual = XmlUtilities.findXmlNodeByName(document, "age");
        assertEquals("3", actual.getTextContent());
    }

    @Test
    void findXmlNodeByNameNegativeNoNodeWithGivenName() throws IOException, SAXException, ParserConfigurationException {
        Document document = XmlUtilities.xmlStringToDocument(testXml);
        XmlParsingException xmlParsingException = assertThrows(XmlParsingException.class,
                () -> XmlUtilities.findXmlNodeByName(document, "gender"));
        assertEquals("Couldn't find node named \"gender\"", xmlParsingException.getMessage());
    }

    @Test
    void xmlUtilitiesConstructorAccessNegative() throws NoSuchMethodException {
        Constructor<XmlUtilities> declaredConstructor = XmlUtilities.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, declaredConstructor::newInstance);
    }
}