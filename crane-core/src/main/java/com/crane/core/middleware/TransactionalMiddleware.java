package com.crane.core.middleware;

import com.crane.core.ConnectionHolder;
import com.crane.core.Context;
import com.crane.core.Handler;
import com.crane.core.TransactionAwareConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;

public class TransactionalMiddleware implements Middleware {

    private final DataSource dataSource;
    private static final Logger LOGGER = LogManager.getLogger(TransactionalMiddleware.class);


    public TransactionalMiddleware(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void apply(Context ctx, Handler next) throws Exception {
        if (!ctx.isTransactional()) {
            Connection connection = dataSource.getConnection();
            ConnectionHolder.set(new TransactionAwareConnection(connection));
            try {
                next.handle(ctx);
            } finally {
                ConnectionHolder.clear();
                connection.close(); // Close the actual connection
            }
            return;
        }

        if (ConnectionHolder.isActive()) {
            next.handle(ctx);
            return;
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            // Set as transactional connection
            ConnectionHolder.setTransactional(new TransactionAwareConnection(connection));

            next.handle(ctx);

            if (!connection.isClosed()) {
                connection.commit();
                LOGGER.debug("Transaction committed successfully");
            }

        } catch (Exception e) {
            if (connection != null && !connection.isClosed()) {
                try {
                    connection.rollback();
                    LOGGER.info("Transaction rolled back due to exception");
                } catch (Exception sqlEx) {
                    LOGGER.error("Failed to roll back transaction", sqlEx);
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                ConnectionHolder.clear();
                try {
                    connection.close(); // Close the actual connection
                } catch (Exception sqlEx) {
                    LOGGER.error("Failed to close connection", sqlEx);
                }
            }
        }
    }
}
