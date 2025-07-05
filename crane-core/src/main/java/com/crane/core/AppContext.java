package com.crane.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class AppContext {

  private final Map<Class<?>, Object> components = new HashMap<>();

  protected  <T> void register(Class<T> type) {
    if (components.containsKey(type)) {
      return; // Already registered
    }

    try {
      Constructor<T> constructor = type.getConstructor(AppContext.class);
      T instance = constructor.newInstance(this); // this = AppContext
      components.put(type, instance);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Component " + type.getName() + " must have a constructor with AppContext");
    } catch (Exception e) {
      throw new RuntimeException("Failed to instantiate component " + type.getName(), e);
    }
  }


  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    T t = (T) components.get(type);
    if (t == null) {
      throw new RuntimeException("No service registered for type " + type);
    }
    return t;
  }

  public boolean contains(Class<?> type) {
    return components.containsKey(type);
  }
}
