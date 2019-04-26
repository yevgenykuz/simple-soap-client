package me.yevgeny.simplesoapclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
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

    private String urlString;
    private String namespaceUri;
    private String wsOperation;
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
     */
    public SimpleSoapClientImpl(String serviceUrl, String namespaceUri, String wsOperation) {
        this.urlString = serviceUrl;
        this.namespaceUri = namespaceUri;
        this.wsOperation = wsOperation;
    }

    @Override
    public String sendSoapRequest(File requestXml) throws IOException, SimpleSoapClientException {
        openConnection();

        OutputStream requestStream = connection.getOutputStream();
        requestStream.write(Files.readAllBytes(requestXml.toPath()));
        requestStream.flush();
        requestStream.close();

        String responseMessage = connection.getResponseMessage();
        if (!responseMessage.equals("OK")) {
            String errorString = String.format("HTTP response was \"%s\"", responseMessage);
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                errorString += String.format(". Server returned:\n\"%s\"", new BufferedReader(
                        new InputStreamReader(errorStream)).lines().collect(Collectors.joining("\n")));
            }
            throw new SimpleSoapClientException(errorString);
        }

        InputStream responseStream = connection.getInputStream();
        String response = new BufferedReader(new InputStreamReader(responseStream)).lines().collect(
                Collectors.joining(System.lineSeparator()));
        requestStream.close();
        closeConnection();

        return response;
    }


    private void openConnection() throws SimpleSoapClientException, IOException {
        if (urlString.isEmpty()) {
            throw new SimpleSoapClientException("URL is required to open an HTTP connection");
        }

        if (namespaceUri.isEmpty()) {
            throw new SimpleSoapClientException("Namespace URI is required to send SOAP requests");
        }

        if (wsOperation.isEmpty()) {
            throw new SimpleSoapClientException("WS operation is required to open an HTTP connection");
        }

        URL url = new URL(String.format("%s.asmx?op=%s", urlString, wsOperation));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", String.format("%s/%s", namespaceUri, wsOperation));
        connection.setDoOutput(true);
    }

    private void closeConnection() {
        connection.disconnect();
    }
}
