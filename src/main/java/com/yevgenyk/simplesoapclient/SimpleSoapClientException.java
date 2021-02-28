package com.yevgenyk.simplesoapclient;

/**
 * {@code SimpleSoapClientException} is used to indicate an exception in {@code SimpleSoapClient}.
 */
public class SimpleSoapClientException extends Exception {

    /**
     * Constructs a new {@code SimpleSoapClientException} with the specified message.
     *
     * @param message
     *         the message
     */
    public SimpleSoapClientException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code SimpleSoapClientException} with the specified message.
     * <p>
     * Used to wrap other exceptions.
     *
     * @param message
     *         The message
     * @param wrappedCause
     *         The wrapped cause
     */
    public SimpleSoapClientException(String message, Throwable wrappedCause) {
        super(message, wrappedCause);
    }
}
