package com.yevgenyk.simplesoapclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests mock a free SOAP service found @http://www.dneonline.com/calculator.asmx.
 */
class SimpleSoapClientImplTest {

    private WireMockServer wireMockServer;

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void sendSoapRequest() throws IOException, SimpleSoapClientException, ParserConfigurationException, SAXException,
            XPathExpressionException, XmlParsingException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(post("/calculator.asmx?op=Add")
                .willReturn(okTextXml(
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + System.lineSeparator() +
                                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + System.lineSeparator() +
                                " <soap:Body>" + System.lineSeparator() +
                                "  <AddResponse xmlns=\"http://tempuri.org/\">" + System.lineSeparator() +
                                "   <AddResult>4</AddResult>" + System.lineSeparator() +
                                "  </AddResponse>" + System.lineSeparator() +
                                " </soap:Body>" + System.lineSeparator() +
                                "</soap:Envelope>")));
        SimpleSoapClient client = new SimpleSoapClientImpl("http://0.0.0.0:8080/calculator", "http://tempuri.org",
                "Add");
        String response = client.sendSoapRequest(new File("src/test/resources/requestExample.xml"));
        String actual = XmlUtilities.getTextContentOfXmlElement(XmlUtilities.xmlStringToDocument(response),
                "AddResult");
        assertEquals("4", actual);
    }

    @Test
    void sendSoapRequestNegativeInternalServerError() throws SimpleSoapClientException {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(post("/calculator.asmx?op=Add1")
                .willReturn(aResponse()
                        .withStatus(HTTP_INTERNAL_ERROR)
                        .withHeader("Content-Type", "text/xml; charset=utf-8")
                        .withBody("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + System.lineSeparator() +
                                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" + System.lineSeparator() +
                                " <soap:Body>" + System.lineSeparator() +
                                "  <soap:Fault>" + System.lineSeparator() +
                                "   <faultcode>" + System.lineSeparator() +
                                "    soap:Client" + System.lineSeparator() +
                                "   </faultcode>" + System.lineSeparator() +
                                "   <faultstring>" + System.lineSeparator() +
                                "    [truncated]System.Web.Services.Protocols.SoapException: " +
                                "Server did not recognize the value of HTTP Header SOAPAction: " +
                                "http://tempuri.org/Add1.\\r\\n   " +
                                "at System.Web.Services.Protocols.Soap11ServerProtocolHelper.RouteRequest()\\r\\n   " +
                                "at Syst" + System.lineSeparator() +
                                "   </faultstring>" + System.lineSeparator() +
                                "  <detail/>" + System.lineSeparator() +
                                "  </soap:Fault>" +
                                " </soap:Body>" + System.lineSeparator() +
                                "</soap:Envelope>")
                ));
        SimpleSoapClient client = new SimpleSoapClientImpl("http://0.0.0.0:8080/calculator", "http://tempuri.org",
                "Add1");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertTrue(simpleSoapClientException.getMessage()
                .contains("HTTP response was \"Server Error\". Server returned:"));
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

    @Test
    void sendSoapRequestNegativeUnreachableServiceUrl() throws SimpleSoapClientException {
        SimpleSoapClientImpl client =
                new SimpleSoapClientImpl("http://0.0.0.0:8080/calculator", "http://tempuri.org", "Add");
        SimpleSoapClientException simpleSoapClientException = assertThrows(SimpleSoapClientException.class,
                () -> client.sendSoapRequest(new File("src/test/resources/requestExample.xml")));
        assertEquals("Couldn't send SOAP request", simpleSoapClientException.getMessage());
    }
}
