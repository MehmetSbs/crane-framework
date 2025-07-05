package com.crane.mail.sender;

import com.crane.mail.exception.MailSendException;
import com.crane.mail.model.Mail;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Main interface for sending emails
 */
public interface MailSender {

    /**
     * Send a single email synchronously
     * @param mail the email to send
     * @throws MailSendException if sending fails
     */
    void send(Mail mail) throws MailSendException;

    /**
     * Send a single email asynchronously
     * @param mail the email to send
     * @return CompletableFuture that completes when email is sent
     */
    CompletableFuture<Void> sendAsync(Mail mail);

    /**
     * Send multiple emails synchronously
     * @param mails collection of emails to send
     * @throws MailSendException if any email fails to send
     */
    void send(Collection<Mail> mails) throws MailSendException;

    /**
     * Send multiple emails asynchronously
     * @param mails collection of emails to send
     * @return CompletableFuture that completes when all emails are sent
     */
    CompletableFuture<Void> sendAsync(Collection<Mail> mails);

    /**
     * Send multiple emails asynchronously with individual result tracking
     * @param mails collection of emails to send
     * @return CompletableFuture with results for each email
     */
    CompletableFuture<Collection<MailSendResult>> sendAsyncWithResults(Collection<Mail> mails);

    /**
     * Test the mail configuration by sending a test email
     * @param testRecipient email address to send test email to
     * @throws MailSendException if test fails
     */
    void testConnection(String testRecipient) throws MailSendException;

    /**
     * Check if the mail sender is properly configured and ready to send emails
     * @return true if ready, false otherwise
     */
    boolean isReady();
}
