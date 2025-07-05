package com.crane.mail.exception;

/**
 * Exception thrown when attachment processing fails
 */
class AttachmentException extends MailSendException {

    public AttachmentException(String message) {
        super(message);
    }

    public AttachmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
