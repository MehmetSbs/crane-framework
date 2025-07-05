package com.crane.core.middleware;


import com.crane.core.Context;
import com.crane.core.Handler;

@FunctionalInterface
public interface Middleware {
  void apply(Context ctx, Handler next) throws Exception;
}
