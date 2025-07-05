package com.crane.mail.exception;

/**
 * Exception thrown when authentication fails
 */
public class MailAuthenticationException extends MailSendException {

    public MailAuthenticationException(String message) {
        super(message);
    }

    public MailAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}