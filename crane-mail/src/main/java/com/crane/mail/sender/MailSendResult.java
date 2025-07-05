package com.crane.mail.sender;

import com.crane.mail.model.Mail;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents the result of sending an email
 */
public class MailSendResult {
    private final Mail mail;
    private final boolean success;
    private final String messageId;
    private final LocalDateTime sentAt;
    private final String errorMessage;
    private final Throwable exception;

    private MailSendResult(Builder builder) {
        this.mail = builder.mail;
        this.success = builder.success;
        this.messageId = builder.messageId;
        this.sentAt = builder.sentAt;
        this.errorMessage = builder.errorMessage;
        this.exception = builder.exception;
    }

    public static Builder success(Mail mail) {
        return new Builder(mail, true);
    }

    public static Builder failure(Mail mail) {
        return new Builder(mail, false);
    }

    public Mail getMail() {
        return mail;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public Optional<String> getMessageId() {
        return Optional.ofNullable(messageId);
    }

    public Optional<LocalDateTime> getSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public Optional<Throwable> getException() {
        return Optional.ofNullable(exception);
    }

    @Override
    public String toString() {
        if (success) {
            return "MailSendResult{success, messageId='" + messageId + "', sentAt=" + sentAt + "}";
        } else {
            return "MailSendResult{failure, error='" + errorMessage + "'}";
        }
    }

    public static class Builder {
        private final Mail mail;
        private final boolean success;
        private String messageId;
        private LocalDateTime sentAt;
        private String errorMessage;
        private Throwable exception;

        private Builder(Mail mail, boolean success) {
            this.mail = mail;
            this.success = success;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public MailSendResult build() {
            if (success && sentAt == null) {
                sentAt = LocalDateTime.now();
            }
            return new MailSendResult(this);
        }
    }
}
