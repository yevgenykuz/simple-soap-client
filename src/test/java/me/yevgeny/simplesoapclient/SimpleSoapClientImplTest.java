package me.yevgeny.simplesoapclient;

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
    void sendSoapRequestNegativeInternalServerError() {
        SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org",
                "Add1");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertTrue(simpleSoapClientException.getMessage()
                .contains("HTTP response was \"Internal Server Error\". Server returned:"));
    }

    @Test
    void sendSoapRequestNegativeNoUrlString() {
        SimpleSoapClient client = new SimpleSoapClientImpl("", "http://tempuri.org", "Add");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertEquals("URL is required to open an HTTP connection", simpleSoapClientException.getMessage());
    }

    @Test
    void sendSoapRequestNegativeNoNamespaceUriString() {
        SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "", "Add");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertEquals("Namespace URI is required to send SOAP requests", simpleSoapClientException.getMessage());
    }

    @Test
    void sendSoapRequestNegativeNoWsOperationString() {
        SimpleSoapClient client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org",
                "");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertEquals("WS operation is required to open an HTTP connection", simpleSoapClientException.getMessage());
    }
}