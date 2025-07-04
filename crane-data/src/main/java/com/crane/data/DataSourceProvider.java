package com.crane.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceProvider {

  private static HikariDataSource dataSource;

  public static void init(String jdbcUrl, String username, String password) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setIdleTimeout(30000);
    config.setConnectionTimeout(3000);
    config.setLeakDetectionThreshold(5000);

    dataSource = new HikariDataSource(config);
  }

  public static Connection getConnection() throws SQLException {
    if (dataSource == null) {
      throw new IllegalStateException("DataSourceProvider is not initialized. Call init() first.");
    }
    return dataSource.getConnection();
  }

  public static void shutdown() {
    if (dataSource != null) {
      dataSource.close();
    }
  }
}
