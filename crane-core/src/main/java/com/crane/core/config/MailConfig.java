package com.crane.core.config;
import java.util.Properties;

/**
 * Configuration for mail sending
 */
public class MailConfig {
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private boolean ssl = false;
    private boolean auth = false;
    private boolean startTls = true;
    private String defaultFrom;
    private String defaultFromName;
    private int connectionTimeout = 10000; // 10 seconds
    private int timeout = 10000; // 10 seconds
    private int writeTimeout = 10000; // 10 seconds
    private boolean debug = false;
    private Properties additionalProperties = new Properties();

    // Default constructor
    public MailConfig() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }

    public String getDefaultFrom() {
        return defaultFrom;
    }

    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    public String getDefaultFromName() {
        return defaultFromName;
    }

    public void setDefaultFromName(String defaultFromName) {
        this.defaultFromName = defaultFromName;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Properties getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Properties additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    /**
     * Validate the configuration
     * @throws IllegalArgumentException if configuration is invalid
     */
    public void validate() {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Mail host is required");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Mail port must be between 1 and 65535");
        }
        if (connectionTimeout < 0) {
            throw new IllegalArgumentException("Connection timeout must be non-negative");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be non-negative");
        }
        if (writeTimeout < 0) {
            throw new IllegalArgumentException("Write timeout must be non-negative");
        }
    }

    /**
     * Convert configuration to JavaMail Properties
     * @return Properties for JavaMail Session
     */
    public Properties toProperties() {
        Properties props = new Properties();

        // Basic SMTP properties
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", String.valueOf(auth));

        // Authentication
        if (username != null && !username.trim().isEmpty()) {
            props.put("mail.smtp.auth", "true");
        }

        // Security
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
        }
        if (startTls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        // Timeouts
        props.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
        props.put("mail.smtp.timeout", String.valueOf(timeout));
        props.put("mail.smtp.writetimeout", String.valueOf(writeTimeout));

        // Debug
        if (debug) {
            props.put("mail.debug", "true");
        }

        // Additional properties
        props.putAll(additionalProperties);

        return props;
    }

    public static class Builder {
        private final MailConfig config = new MailConfig();

        public Builder host(String host) {
            config.setHost(host);
            return this;
        }

        public Builder port(int port) {
            config.setPort(port);
            return this;
        }

        public Builder auth(boolean auth) {
            config.setAuth(auth);
            return this;
        }

        public Builder username(String username) {
            config.setUsername(username);
            return this;
        }

        public Builder password(String password) {
            config.setPassword(password);
            return this;
        }

        public Builder ssl(boolean ssl) {
            config.setSsl(ssl);
            return this;
        }

        public Builder startTls(boolean startTls) {
            config.setStartTls(startTls);
            return this;
        }

        public Builder defaultFrom(String defaultFrom) {
            config.setDefaultFrom(defaultFrom);
            return this;
        }

        public Builder defaultFromName(String defaultFromName) {
            config.setDefaultFromName(defaultFromName);
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            config.setConnectionTimeout(connectionTimeout);
            return this;
        }

        public Builder timeout(int timeout) {
            config.setTimeout(timeout);
            return this;
        }

        public Builder writeTimeout(int writeTimeout) {
            config.setWriteTimeout(writeTimeout);
            return this;
        }

        public Builder debug(boolean debug) {
            config.setDebug(debug);
            return this;
        }

        public Builder additionalProperty(String key, String value) {
            config.getAdditionalProperties().put(key, value);
            return this;
        }

        public MailConfig build() {
            config.validate();
            return config;
        }
    }

    @Override
    public String toString() {
        return "MailConfiguration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", ssl=" + ssl +
                ", startTls=" + startTls +
                ", defaultFrom='" + defaultFrom + '\'' +
                ", defaultFromName='" + defaultFromName + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", timeout=" + timeout +
                ", writeTimeout=" + writeTimeout +
                ", debug=" + debug +
                '}';
    }
}
