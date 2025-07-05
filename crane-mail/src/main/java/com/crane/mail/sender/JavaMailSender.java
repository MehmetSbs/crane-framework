package com.crane.mail.sender;

import com.crane.core.config.MailConfig;
import com.crane.mail.exception.*;
import com.crane.mail.model.Attachment;
import com.crane.mail.model.Mail;
import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaMail-based implementation of MailSender
 */
public class JavaMailSender implements MailSender {

    private static final Logger LOGGER = LogManager.getLogger(JavaMailSender.class);


    private final MailConfig configuration;
    private final Session session;
    private final ExecutorService executorService;
    private volatile boolean ready = false;

    public JavaMailSender(MailConfig configuration) {
        this.configuration = configuration;
        this.session = createSession();
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "crane-mail-sender");
            t.setDaemon(true);
            return t;
        });
        this.ready = validateConfiguration();
    }

    private Session createSession() {
        Properties props = configuration.toProperties();

        if (configuration.getUsername() != null && !configuration.getUsername().trim().isEmpty()) {
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(configuration.getUsername(), configuration.getPassword());
                }
            });
        } else {
            return Session.getInstance(props);
        }
    }

    private boolean validateConfiguration() {
        try {
            configuration.validate();
            return true;
        } catch (Exception e) {
            LOGGER.error("Mail configuration validation failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void send(Mail mail) throws MailSendException {
        if (!ready) {
            throw new MailConfigurationException("Mail sender is not properly configured");
        }

        try {
            MimeMessage message = createMimeMessage(mail);
            Transport.send(message);
        } catch (MessagingException e) {
            throw handleMessagingException(e, mail);
        } catch (Exception e) {
            throw new MailSendException("Failed to send email", e);
        }
    }

    @Override
    public CompletableFuture<Void> sendAsync(Mail mail) {
        return CompletableFuture.runAsync(() -> {
            try {
                send(mail);
            } catch (MailSendException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    @Override
    public void send(Collection<Mail> mails) throws MailSendException {
        for (Mail mail : mails) {
            send(mail);
        }
    }

    @Override
    public CompletableFuture<Void> sendAsync(Collection<Mail> mails) {
        return CompletableFuture.runAsync(() -> {
            try {
                send(mails);
            } catch (MailSendException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Collection<MailSendResult>> sendAsyncWithResults(Collection<Mail> mails) {
        return CompletableFuture.supplyAsync(() -> {
            List<MailSendResult> results = new ArrayList<>();

            for (Mail mail : mails) {
                try {
                    send(mail);
                    results.add(MailSendResult.success(mail).build());
                } catch (MailSendException e) {
                    results.add(MailSendResult.failure(mail)
                            .errorMessage(e.getMessage())
                            .exception(e)
                            .build());
                }
            }

            return results;
        }, executorService);
    }

    @Override
    public void testConnection(String testRecipient) throws MailSendException {
        Mail testMail = Mail.builder()
                .to(testRecipient)
                .subject("Test Email from Crane Mail")
                .textContent("This is a test email to verify mail configuration.")
                .build();

        send(testMail);
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    private MimeMessage createMimeMessage(Mail mail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);

        // Set sender
        String fromAddress = mail.getFrom() != null ? mail.getFrom() : configuration.getDefaultFrom();
        if (fromAddress == null) {
            throw new MessagingException("No sender address specified");
        }

        if (configuration.getDefaultFromName() != null) {
            message.setFrom(new InternetAddress(fromAddress, configuration.getDefaultFromName()));
        } else {
            message.setFrom(new InternetAddress(fromAddress));
        }

        // Set recipients
        setRecipients(message, mail);

        // Set subject
        message.setSubject(mail.getSubject(), "UTF-8");

        // Set priority
        if (mail.getPriority() != null) {
            message.setHeader("X-Priority", mail.getPriority().getValue());
        }

        // Set custom headers
        for (Map.Entry<String, String> header : mail.getHeaders().entrySet()) {
            message.setHeader(header.getKey(), header.getValue());
        }

        // Set content
        if (mail.hasAttachments()) {
            setMultipartContent(message, mail);
        } else {
            setSimpleContent(message, mail);
        }

        // Set sent date
        message.setSentDate(new Date());

        return message;
    }

    private void setRecipients(MimeMessage message, Mail mail) throws MessagingException {
        // TO recipients
        for (String to : mail.getTo()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        }

        // CC recipients
        for (String cc : mail.getCc()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
        }

        // BCC recipients
        for (String bcc : mail.getBcc()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
        }
    }

    private void setSimpleContent(MimeMessage message, Mail mail) throws MessagingException {
        if (mail.hasHtmlContent() && mail.hasTextContent()) {
            // Mixed content
            MimeMultipart multipart = new MimeMultipart("alternative");

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mail.getTextContent(), "UTF-8");
            multipart.addBodyPart(textPart);

            // HTML part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mail.getHtmlContent(), "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);
        } else if (mail.hasHtmlContent()) {
            // HTML only
            message.setContent(mail.getHtmlContent(), "text/html; charset=UTF-8");
        } else {
            // Text only
            message.setText(mail.getTextContent(), "UTF-8");
        }
    }

    private void setMultipartContent(MimeMessage message, Mail mail) throws MessagingException {
        MimeMultipart multipart = new MimeMultipart("mixed");

        // Add content part
        MimeBodyPart contentPart = new MimeBodyPart();
        if (mail.hasHtmlContent() && mail.hasTextContent()) {
            MimeMultipart contentMultipart = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mail.getTextContent(), "UTF-8");
            contentMultipart.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mail.getHtmlContent(), "text/html; charset=UTF-8");
            contentMultipart.addBodyPart(htmlPart);

            contentPart.setContent(contentMultipart);
        } else if (mail.hasHtmlContent()) {
            contentPart.setContent(mail.getHtmlContent(), "text/html; charset=UTF-8");
        } else {
            contentPart.setText(mail.getTextContent(), "UTF-8");
        }

        multipart.addBodyPart(contentPart);

        // Add attachments
        for (Attachment attachment : mail.getAttachments()) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.setDataHandler(new DataHandler(new AttachmentDataSource(attachment)));
                attachmentPart.setFileName(attachment.getFilename());
                multipart.addBodyPart(attachmentPart);
            } catch (Exception e) {
                throw new MessagingException("Failed to attach file: " + attachment.getFilename(), e);
            }
        }

        message.setContent(multipart);
    }

    private MailSendException handleMessagingException(MessagingException e, Mail mail) {
        String errorMessage = e.getMessage();

        if (e instanceof AuthenticationFailedException) {
            return new MailAuthenticationException("Authentication failed: " + errorMessage, e);
        } else if (e instanceof SendFailedException) {
            return new MailSendException("Failed to send email to: " + mail.getTo() + ". " + errorMessage, e);
        } else if (errorMessage != null) {
            if (errorMessage.contains("timeout") || errorMessage.contains("Connection timed out")) {
                return new MailTimeoutException("Mail sending timed out: " + errorMessage, e);
            } else if (errorMessage.contains("Connection refused") || errorMessage.contains("Unknown host")) {
                return new MailConnectionException("Failed to connect to mail server: " + errorMessage, e);
            }
        }

        return new MailSendException("Failed to send email: " + errorMessage, e);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // DataSource implementation for attachments
    private static class AttachmentDataSource implements jakarta.activation.DataSource {
        private final Attachment attachment;

        public AttachmentDataSource(Attachment attachment) {
            this.attachment = attachment;
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            try {
                return attachment.getSource().getInputStream();
            } catch (Exception e) {
                throw new IOException("Failed to get attachment input stream", e);
            }
        }

        @Override
        public java.io.OutputStream getOutputStream() throws IOException {
            throw new IOException("Not supported");
        }

        @Override
        public String getContentType() {
            return attachment.getContentType();
        }

        @Override
        public String getName() {
            return attachment.getFilename();
        }
    }
}