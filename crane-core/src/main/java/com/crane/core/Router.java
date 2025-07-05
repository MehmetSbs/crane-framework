package com.crane.core;

import java.util.HashMap;
import java.util.Map;

public class Router {

  private final Map<String, RouteInfo> getRoutes = new HashMap<>();
  private final Map<String, RouteInfo> postRoutes = new HashMap<>();
  private final Map<String, RouteInfo> putRoutes = new HashMap<>();
  private final Map<String, RouteInfo> deleteRoutes = new HashMap<>();

  public void get(String path, Handler handler) {
    getRoutes.put(path, new RouteInfo(handler, false));
  }

  public void post(String path, Handler handler) {
    postRoutes.put(path, new RouteInfo(handler, false));
  }

  public void put(String path, Handler handler) {
    putRoutes.put(path, new RouteInfo(handler, false));
  }

  public void delete(String path, Handler handler) {
    deleteRoutes.put(path, new RouteInfo(handler, false));
  }

  public void postTransactional(String path, Handler handler) {
    postRoutes.put(path, new RouteInfo(handler, true));
  }

  public void putTransactional(String path, Handler handler) {
    putRoutes.put(path, new RouteInfo(handler, true));
  }

  public void deleteTransactional(String path, Handler handler) {
    deleteRoutes.put(path, new RouteInfo(handler, true));
  }

  public RouteInfo route(String method, String path) {
    return switch (method.toUpperCase()) {
      case "GET" -> getRoutes.get(path);
      case "POST" -> postRoutes.get(path);
      case "PUT" -> putRoutes.get(path);
      case "DELETE" -> deleteRoutes.get(path);
      default -> null;
    };
  }

}
