package com.yevgenyk.simplesoapclient;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests use a free SOAP service found @http://www.dneonline.com/calculator.asmx.
 */
class SimpleSoapClientImplTest {

    @Test
    void sendSoapRequest() throws IOException, SimpleSoapClientException, ParserConfigurationException, SAXException,
            XPathExpressionException, XmlParsingException {
        SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org",
                "Add");
        String response = client.sendSoapRequest(new File("src/test/resources/requestExample.xml"));
        String actual = XmlUtilities.getTextContentOfXmlElement(XmlUtilities.xmlStringToDocument(response),
                "AddResult");
        assertEquals("4", actual);
    }

    @Test
    void sendSoapRequestNegativeInternalServerError() throws SimpleSoapClientException {
        SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org",
                "Add1");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertTrue(simpleSoapClientException.getMessage()
                .contains("HTTP response was \"Internal Server Error\". Server returned:"));
    }

    @Test
    void sendSoapRequestNegativeNoUrlString() {
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> new SimpleSoapClientImpl("", "http://tempuri.org", "Add"));
        assertEquals("URL is required to open an HTTP connection", simpleSoapClientException.getMessage());
    }

    @Test
    void sendSoapRequestNegativeNoNamespaceUriString() {
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "", "Add"));
        assertEquals("Namespace URI is required to send SOAP requests", simpleSoapClientException.getMessage());
    }

    @Test
    void sendSoapRequestNegativeNoWsOperationString() {
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org", ""));
        assertEquals("WS operation is required to open an HTTP connection", simpleSoapClientException.getMessage());
    }
}