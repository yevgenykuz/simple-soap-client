package com.yevgenyk.simplesoapclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * {@code SimpleSoapClientImpl} is an implementation of {@code SimpleSoapClient}.
 * <p>
 * A client sends each SOAP request by:
 * <ol>
 * <li>Opening an HTTP connection to the provided URL</li>
 * <li>Setting the relevant headers (HTTP POST and SOAP headers)</li>
 * <li>Sending the request</li>
 * <li>Closing the HTTP connection</li>
 * </ol>
 */
public class SimpleSoapClientImpl implements SimpleSoapClient {

    private final String urlString;
    private final String namespaceUri;
    private final String wsOperation;
    private HttpURLConnection connection;

    /**
     * Creates an instance of {@code SimpleSoapClientImpl}. Each client holds an SOAP URL and a namespace URI to send
     * SOAP requests and get responses.
     *
     * @param serviceUrl
     *         URL to a WS service - "http://www.dneonline.com/calculator" for example
     * @param namespaceUri
     *         Namespace URI for XML mapping as represented in WSDL - "http://tempuri.org" for example
     * @param wsOperation
     *         WS operation as represented in WSDL - "Add" for example
     * @throws SimpleSoapClientException
     *         If one of thr parameters is empty or null
     */
    public SimpleSoapClientImpl(String serviceUrl, String namespaceUri, String wsOperation)
            throws SimpleSoapClientException {
        this.urlString = serviceUrl;
        this.namespaceUri = namespaceUri;
        this.wsOperation = wsOperation;
        checkConnectionParameters();
    }

    @Override
    public String sendSoapRequest(File requestXml) throws SimpleSoapClientException {
        try {
            openConnection();
            try (OutputStream requestStream = connection.getOutputStream()) {
                requestStream.write(Files.readAllBytes(requestXml.toPath()));
                requestStream.flush();
                String responseMessage = connection.getResponseMessage();
                if (!responseMessage.equals("OK")) {
                    String errorString = String.format("HTTP response was \"%s\"", responseMessage);
                    InputStream errorStream = connection.getErrorStream();
                    Scanner errorStreamScanner = new Scanner(errorStream).useDelimiter("\\A");
                    errorString += String.format(". Server returned:\n\"%s\"",
                            errorStreamScanner.hasNext() ? errorStreamScanner.next() : "");
                    throw new SimpleSoapClientException(errorString);
                }
            }
            try (InputStream responseStream = connection.getInputStream()) {
                return new BufferedReader(new InputStreamReader(responseStream)).lines()
                        .collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            throw new SimpleSoapClientException("Couldn't send SOAP request", e);
        } finally {
            closeConnection();
        }
    }

    private void openConnection() throws IOException {
        URL url = new URL(String.format("%s.asmx?op=%s", urlString, wsOperation));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", String.format("%s/%s", namespaceUri, wsOperation));
        connection.setDoOutput(true);
    }

    private void closeConnection() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private void checkConnectionParameters() throws SimpleSoapClientException {
        if (urlString == null || urlString.isEmpty()) {
            throw new SimpleSoapClientException("URL is required to open an HTTP connection");
        }
        if (namespaceUri == null || namespaceUri.isEmpty()) {
            throw new SimpleSoapClientException("Namespace URI is required to send SOAP requests");
        }
        if (wsOperation == null || wsOperation.isEmpty()) {
            throw new SimpleSoapClientException("WS operation is required to open an HTTP connection");
        }
    }
}
