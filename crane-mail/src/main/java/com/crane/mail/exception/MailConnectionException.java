package com.crane.mail.exception;

/**
 * Exception thrown when connection to mail server fails
 */
public class MailConnectionException extends MailSendException {

    public MailConnectionException(String message) {
        super(message);
    }

    public MailConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
