package com.crane.mail.exception;

/**
 * Base exception for mail sending operations
 */
public class MailSendException extends Exception {

    public MailSendException(String message) {
        super(message);
    }

    public MailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailSendException(Throwable cause) {
        super(cause);
    }
}
