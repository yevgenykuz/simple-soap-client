package me.yevgeny.simplesoapclient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These tests use a free SOAP service found @http://www.dneonline.com/calculator.asmx.
 */
class SimpleSoapClientImplTest {

    static private SimpleSoapClient client;

    @BeforeAll
    static void setUp() {
        client = new SimpleSoapClientImpl("http://www.dneonline.com/calculator", "http://tempuri.org", "Add");
    }

    @Test
    void sendSoapRequest() throws IOException, SimpleSoapClientException, ParserConfigurationException, SAXException,
                                  XPathExpressionException, XmlParsingException {
        String response = client.sendSoapRequest(new File("src/test/resources/requestExample.xml"));
        String actual = XmlUtilities.getTextContentOfXmlElement(XmlUtilities.xmlStringToDocument(response),
                "AddResult");
        assertEquals("4", actual);
    }
}