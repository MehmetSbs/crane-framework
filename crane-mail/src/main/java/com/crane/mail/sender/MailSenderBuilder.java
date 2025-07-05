package com.crane.mail.sender;

import com.crane.core.config.MailConfig;

/**
 * Builder for creating MailSender instances
 */
public class MailSenderBuilder {
    private MailConfig configuration;

    private MailSenderBuilder() {
        this.configuration = new MailConfig();
    }

    public static MailSenderBuilder create() {
        return new MailSenderBuilder();
    }

    public MailSenderBuilder host(String host) {
        configuration.setHost(host);
        return this;
    }

    public MailSenderBuilder port(int port) {
        configuration.setPort(port);
        return this;
    }

    public MailSenderBuilder username(String username) {
        configuration.setUsername(username);
        return this;
    }

    public MailSenderBuilder password(String password) {
        configuration.setPassword(password);
        return this;
    }

    public MailSenderBuilder ssl(boolean ssl) {
        configuration.setSsl(ssl);
        return this;
    }

    public MailSenderBuilder startTls(boolean startTls) {
        configuration.setStartTls(startTls);
        return this;
    }

    public MailSenderBuilder defaultFrom(String defaultFrom) {
        configuration.setDefaultFrom(defaultFrom);
        return this;
    }

    public MailSenderBuilder defaultFromName(String defaultFromName) {
        configuration.setDefaultFromName(defaultFromName);
        return this;
    }

    public MailSenderBuilder connectionTimeout(int timeout) {
        configuration.setConnectionTimeout(timeout);
        return this;
    }

    public MailSenderBuilder timeout(int timeout) {
        configuration.setTimeout(timeout);
        return this;
    }

    public MailSenderBuilder writeTimeout(int timeout) {
        configuration.setWriteTimeout(timeout);
        return this;
    }

    public MailSenderBuilder debug(boolean debug) {
        configuration.setDebug(debug);
        return this;
    }

    public MailSenderBuilder configuration(MailConfig configuration) {
        this.configuration = configuration;
        return this;
    }

    // Convenience methods for common providers
    public MailSenderBuilder gmail(String username, String password) {
        return host("smtp.gmail.com")
                .port(587)
                .username(username)
                .password(password)
                .startTls(true)
                .defaultFrom(username);
    }

    public MailSenderBuilder outlook(String username, String password) {
        return host("smtp-mail.outlook.com")
                .port(587)
                .username(username)
                .password(password)
                .startTls(true)
                .defaultFrom(username);
    }

    public MailSenderBuilder yahoo(String username, String password) {
        return host("smtp.mail.yahoo.com")
                .port(587)
                .username(username)
                .password(password)
                .startTls(true)
                .defaultFrom(username);
    }

    public MailSenderBuilder amazonSes(String region, String username, String password) {
        return host("email-smtp." + region + ".amazonaws.com")
                .port(587)
                .username(username)
                .password(password)
                .startTls(true);
    }

    public MailSenderBuilder sendGrid(String apiKey) {
        return host("smtp.sendgrid.net")
                .port(587)
                .username("apikey")
                .password(apiKey)
                .startTls(true);
    }

    public MailSenderBuilder mailgun(String domain, String apiKey) {
        return host("smtp.mailgun.org")
                .port(587)
                .username("postmaster@" + domain)
                .password(apiKey)
                .startTls(true);
    }

    public MailSender build() {
        return new JavaMailSender(configuration);
    }
}
