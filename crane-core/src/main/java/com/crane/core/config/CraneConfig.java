package com.crane.core.config;

public class CraneConfig {

  private ServerConfig server = new ServerConfig();
  private DatabaseConfig database;

  public ServerConfig getServer() {
    return server;
  }

  public void setServer(ServerConfig server) {
    this.server = server;
  }

  public DatabaseConfig getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseConfig databaseConfig) {
    this.database = databaseConfig;
  }
}
