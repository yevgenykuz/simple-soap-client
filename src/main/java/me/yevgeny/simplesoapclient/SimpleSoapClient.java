package me.yevgeny.simplesoapclient;

import java.io.File;
import java.io.IOException;

/**
 * The {@code SimpleSoapClient} interface represents a bare-minimum SOAP client object. The client must be able to send
 * an XML request to a SOAP endpoint and read the response.
 */
public interface SimpleSoapClient {

    /**
     * Send a SOAP request in XML format from an .xml file, get the full response XML as string.
     *
     * @param requestXml
     *         The .xml file that contains the SOAP request in XML format.
     *
     * @return The .xml response as string
     *
     * @throws IOException
     *         If {@code requestXml} file was not found
     * @throws SimpleSoapClientException
     *         If one or more of the HTTP connection required arguments are missing, or If HTTP host returned any
     *         response code that is not "OK"
     */
    String sendSoapRequest(File requestXml) throws IOException, SimpleSoapClientException;
}
