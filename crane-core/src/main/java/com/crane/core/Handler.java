package com.crane.core;


@FunctionalInterface
public interface Handler {

  void handle(Context ctx) throws Exception;
}
