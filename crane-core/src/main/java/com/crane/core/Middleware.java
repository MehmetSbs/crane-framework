package com.crane.core;


@FunctionalInterface
public interface Middleware {
  void apply(Context ctx, Handler next) throws Exception;
}
