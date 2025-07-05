package com.crane.mail.model;

import java.util.*;

/**
 * Represents an email message with all necessary components
 */
public class Mail {
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String textContent;
    private String htmlContent;
    private List<Attachment> attachments;
    private Map<String, String> headers;
    private Priority priority;

    public enum Priority {
        HIGH("1"),
        NORMAL("3"),
        LOW("5");

        private final String value;

        Priority(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Private constructor for builder pattern
    private Mail(Builder builder) {
        this.from = builder.from;
        this.to = new ArrayList<>(builder.to);
        this.cc = new ArrayList<>(builder.cc);
        this.bcc = new ArrayList<>(builder.bcc);
        this.subject = builder.subject;
        this.textContent = builder.textContent;
        this.htmlContent = builder.htmlContent;
        this.attachments = new ArrayList<>(builder.attachments);
        this.headers = new HashMap<>(builder.headers);
        this.priority = builder.priority;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return Collections.unmodifiableList(to);
    }

    public List<String> getCc() {
        return Collections.unmodifiableList(cc);
    }

    public List<String> getBcc() {
        return Collections.unmodifiableList(bcc);
    }

    public String getSubject() {
        return subject;
    }

    public String getTextContent() {
        return textContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean hasTextContent() {
        return textContent != null && !textContent.trim().isEmpty();
    }

    public boolean hasHtmlContent() {
        return htmlContent != null && !htmlContent.trim().isEmpty();
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public static class Builder {
        private String from;
        private List<String> to = new ArrayList<>();
        private List<String> cc = new ArrayList<>();
        private List<String> bcc = new ArrayList<>();
        private String subject;
        private String textContent;
        private String htmlContent;
        private List<Attachment> attachments = new ArrayList<>();
        private Map<String, String> headers = new HashMap<>();
        private Priority priority = Priority.NORMAL;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String... to) {
            this.to.addAll(Arrays.asList(to));
            return this;
        }

        public Builder to(Collection<String> to) {
            this.to.addAll(to);
            return this;
        }

        public Builder cc(String... cc) {
            this.cc.addAll(Arrays.asList(cc));
            return this;
        }

        public Builder cc(Collection<String> cc) {
            this.cc.addAll(cc);
            return this;
        }

        public Builder bcc(String... bcc) {
            this.bcc.addAll(Arrays.asList(bcc));
            return this;
        }

        public Builder bcc(Collection<String> bcc) {
            this.bcc.addAll(bcc);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder htmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public Builder attachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        public Builder attachments(Collection<Attachment> attachments) {
            this.attachments.addAll(attachments);
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Mail build() {
            validate();
            return new Mail(this);
        }

        private void validate() {
            if (to.isEmpty()) {
                throw new IllegalArgumentException("Mail must have at least one recipient");
            }
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("Mail must have a subject");
            }
            if (!hasTextContent() && !hasHtmlContent()) {
                throw new IllegalArgumentException("Mail must have either text or HTML content");
            }
        }

        private boolean hasTextContent() {
            return textContent != null && !textContent.trim().isEmpty();
        }

        private boolean hasHtmlContent() {
            return htmlContent != null && !htmlContent.trim().isEmpty();
        }
    }

    @Override
    public String toString() {
        return "Mail{" +
                "from='" + from + '\'' +
                ", to=" + to +
                ", cc=" + cc +
                ", bcc=" + bcc +
                ", subject='" + subject + '\'' +
                ", hasTextContent=" + hasTextContent() +
                ", hasHtmlContent=" + hasHtmlContent() +
                ", attachments=" + attachments.size() +
                ", priority=" + priority +
                '}';
    }
}
