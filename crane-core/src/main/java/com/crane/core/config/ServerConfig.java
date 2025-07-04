package com.crane.core.config;

public class ServerConfig {
  private int port = 8080; // default value
  private String host = "localhost"; // default value


  public int getPort() { return port; }
  public void setPort(int port) { this.port = port; }
  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }
}
