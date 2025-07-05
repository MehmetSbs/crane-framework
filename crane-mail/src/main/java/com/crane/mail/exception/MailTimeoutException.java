package com.crane.mail.exception;

/**
 * Exception thrown when email sending times out
 */
public class MailTimeoutException extends MailSendException {

    public MailTimeoutException(String message) {
        super(message);
    }

    public MailTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
