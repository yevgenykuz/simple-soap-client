package com.yevgenyk.simplesoapclient;

/**
 * {@code XmlParsingException} is used to indicate an exception in {@code XmlUtilities} methods.
 */
public class XmlParsingException extends Exception {
    /**
     * Constructs a new {@code XmlParsingException} with the specified detail message.
     *
     * @param message
     *         the detail message
     */
    public XmlParsingException(String message) {
        super(message);
    }
}
