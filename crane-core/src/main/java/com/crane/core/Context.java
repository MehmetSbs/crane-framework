package com.crane.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Context {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpExchange exchange;

  public Context(HttpExchange exchange) {
    this.exchange = exchange;
  }

  public String method() {
    return exchange.getRequestMethod();
  }

  public String path() {
    return exchange.getRequestURI().getPath();
  }

  public InputStream body() {
    return exchange.getRequestBody();
  }

  public <T> T bodyAs(Class<T> clazz) throws IOException {
    return objectMapper.readValue(exchange.getRequestBody(), clazz);
  }

  public String queryParam(String key) {
    return queryParams().get(key);
  }

  public Map<String, String> queryParams() {
    Map<String, String> queryPairs = new HashMap<>();
    URI uri = exchange.getRequestURI();
    String query = uri.getRawQuery();
    if (query == null || query.isEmpty()) {
      return queryPairs;
    }

    for (String pair : query.split("&")) {
      int idx = pair.indexOf("=");
      String k = idx > 0 ? decode(pair.substring(0, idx)) : pair;
      String v = idx > 0 && pair.length() > idx + 1 ? decode(pair.substring(idx + 1)) : "";
      queryPairs.put(k, v);
    }
    return queryPairs;
  }

  private String decode(String s) {
    return URLDecoder.decode(s, StandardCharsets.UTF_8);
  }

  public void textResponse(String response) throws IOException {
    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  public void jsonResponse(String json) throws IOException {
    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    exchange.sendResponseHeaders(200, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  public void jsonResponse(Object object) throws IOException {
    String s = objectMapper.writeValueAsString(object);
    jsonResponse(s);
  }

  public void errorResponse(Object object) throws IOException {
    String json = objectMapper.writeValueAsString(object);
    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    exchange.sendResponseHeaders(500, bytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(bytes);
    }
  }

  public void statusResponse(int code) throws IOException {
    exchange.sendResponseHeaders(code, -1);
  }
}