package com.crane.core;

import java.sql.Connection;

public class ConnectionHolder {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> transactionActive = new ThreadLocal<>();

    public static void set(Connection connection) {
        connectionHolder.set(connection);
        transactionActive.set(false); // Default to non-transactional
    }

    public static void setTransactional(Connection connection) {
        connectionHolder.set(connection);
        transactionActive.set(true);
    }

    public static Connection get() {
        return connectionHolder.get();
    }

    public static boolean isActive() {
        return connectionHolder.get() != null;
    }

    public static boolean isTransactional() {
        Boolean active = transactionActive.get();
        return active != null && active;
    }

    public static void clear() {
        connectionHolder.remove();
        transactionActive.remove();
    }
}