package me.yevgeny.simplesoapclient;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * {@code XmlUtilities} provides basic XML parsing utility functions to print and parse XML {@code Document} or string.
 */
public final class XmlUtilities {

    /**
     * Convert XML string to XML {@code Document}.
     *
     * @param xmlString
     *         The string that represent an XML message
     * @return The XML response as XML {@code Document}
     * @throws ParserConfigurationException
     *         If XML parser instantiation failed
     * @throws IOException
     *         If an I/O error occurs
     * @throws SAXException
     *         If XML parsing failed
     */
    public static Document xmlStringToDocument(String xmlString) throws ParserConfigurationException, IOException,
            SAXException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
        doc.setXmlStandalone(true);
        return doc;
    }

    /**
     * Convert an XML {@code Document} to string.
     *
     * @param doc
     *         An XML {@code Document}
     * @param omitXmlDeclaration
     *         if <b>true</b> - "<?xml version= ..." will be omitted
     * @return The string representing the XML {@code Document}
     * @throws TransformerException
     *         If {@code Document} transformation to string fails
     */
    public static String xmlDocumentToString(Document doc, boolean omitXmlDeclaration) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration ? "yes" : "no");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    /**
     * Gets the value of an XML field.
     *
     * @param path
     *         A {@code path} ({@code XPath} format) to the required XML field, or the field name if path is unknown
     * @param document
     *         The document to traverse to find the relevant XML field
     * @return The value of an XML field
     * @throws XPathExpressionException
     *         If the provided {@code path} couldn't compile to an {@code XPath}
     * @throws XmlParsingException
     *         If the provided {@code path} doesn't point to an XML field
     */
    public static String getTextContentOfXmlElement(Document document, final String path) throws
            XPathExpressionException, XmlParsingException {
        if (null == path || path.isEmpty()) {
            throw new XmlParsingException("Path to field parameter was not set correctly");
        }
        Node validationNode;
        if (path.contains("/")) {
            validationNode = findXmlNodeByXPath(document, path.trim());
        } else {
            try {
                validationNode = findXmlNodeByXPath(document, "//" + path.trim());
            } catch (Exception e) { // XPath evaluation failed, try to find the node by manually traversing the document
                validationNode = findXmlNodeByName(document, path.trim());
            }
        }
        String textContent = validationNode.getTextContent();
        if (null == textContent || textContent.isEmpty()) {
            throw new XmlParsingException(String.format("No text content was found at \"%s\"", path));
        }
        return textContent;
    }

    /**
     * Finds the first XML node in the given {@code path} ({@code XPath} format) and returns it.
     *
     * @param document
     *         The document to traverse
     * @param path
     *         The path of the wanted node, "//zoo/duck/age" for example
     * @return The <b>first</b> node named {@code nodeName}
     * @throws XPathExpressionException
     *         If path couldn't compile
     * @throws XmlParsingException
     *         If no node found in given {@code path}
     */
    public static Node findXmlNodeByXPath(Document document, String path) throws XPathExpressionException,
            XmlParsingException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node node;
        node = (Node) xpath.compile(path).evaluate(document.getDocumentElement(), XPathConstants.NODE);
        if (null == node) {
            throw new XmlParsingException(String.format("Couldn't find node in the following path: \"%s\"", path));
        }
        return node;
    }

    /**
     * Finds the <b>first</b> XML node that's named {@code nodeName} and returns it.
     *
     * @param document
     *         The document to traverse
     * @param nodeName
     *         The node name to find
     * @return The <b>first</b> node named {@code nodeName}
     * @throws XmlParsingException
     *         If no node found in with given {@code nodeName}
     */
    public static Node findXmlNodeByName(Document document, String nodeName) throws XmlParsingException {
        Node node = findChildNode(document.getChildNodes(), nodeName);
        if (null == node) {
            throw new XmlParsingException(String.format("Couldn't find node named \"%s\"", nodeName));
        }
        return node;
    }

    /**
     * Traverses recursively over a {@code NodeList} to find a node named {@code nodeName}.
     *
     * @param nodeList
     *         The nodes to traverse
     * @param nodeName
     *         The node name to find
     * @return The <b>first</b> node named {@code nodeName} or <b>null</b> if none found
     */
    private static Node findChildNode(NodeList nodeList, String nodeName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equalsIgnoreCase(nodeName)) {
                    return node;
                }

                Node xmlNodeByName = findChildNode(node.getChildNodes(), nodeName);
                if (null != xmlNodeByName) {
                    return xmlNodeByName;
                }
            }
        }
        return null;
    }

    private XmlUtilities() {
        throw new AssertionError();
    }
}
