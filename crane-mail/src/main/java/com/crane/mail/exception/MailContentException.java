package com.crane.mail.exception;

/**
 * Exception thrown when email content is invalid
 */
class MailContentException extends MailSendException {

    public MailContentException(String message) {
        super(message);
    }

    public MailContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
