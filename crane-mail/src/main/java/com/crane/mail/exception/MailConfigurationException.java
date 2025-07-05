package com.crane.mail.exception;

/**
 * Exception thrown when mail configuration is invalid
 */
public class MailConfigurationException extends MailSendException {

    public MailConfigurationException(String message) {
        super(message);
    }

    public MailConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}