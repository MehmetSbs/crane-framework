package com.crane.core;

import java.util.HashMap;
import java.util.Map;

public class Router {

  private final Map<String, Handler> getRoutes = new HashMap<>();
  private final Map<String, Handler> postRoutes = new HashMap<>();
  private final Map<String, Handler> putRoutes = new HashMap<>();
  private final Map<String, Handler> deleteRoutes = new HashMap<>();

  public void get(String path, Handler handler) {
    getRoutes.put(path, handler);
  }

  public void post(String path, Handler handler) {
    postRoutes.put(path, handler);
  }

  public void put(String path, Handler handler) {
    putRoutes.put(path, handler);
  }

  public void delete(String path, Handler handler) {
    deleteRoutes.put(path, handler);
  }

  public Handler route(String method, String path) {
    return switch (method.toUpperCase()) {
      case "GET" -> getRoutes.get(path);
      case "POST" -> postRoutes.get(path);
      case "PUT" -> putRoutes.get(path);
      case "DELETE" -> deleteRoutes.get(path);
      default -> null;
    };
  }

}
