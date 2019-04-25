package me.yevgeny.simplesoapclient;

/**
 * {@code SimpleSoapClientException} is used to indicate an exception in {@code SimpleSoapClient}.
 */
public class SimpleSoapClientException extends Exception {

    /**
     * Constructs a new {@code SimpleSoapClientException} with the specified detail message.
     *
     * @param message
     *         the detail message
     */
    public SimpleSoapClientException(String message) {
        super(message);
    }
}
