package com.crane.core;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

  private final Map<Class<?>, Object> services = new HashMap<>();

  public <T> void register(Class<T> type, T instance) {
    services.put(type, instance);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    T t = (T) services.get(type);
    if (t == null) {
      throw new RuntimeException("No service registered for type " + type);
    }
    return t;
  }

  public boolean contains(Class<?> type) {
    return services.containsKey(type);
  }
}
