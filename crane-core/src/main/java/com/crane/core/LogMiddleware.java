package com.crane.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogMiddleware implements Middleware {

  private static final Logger LOGGER = LogManager.getLogger(LogMiddleware.class);


  @Override
  public void apply(Context ctx, Handler next) throws Exception {
    LOGGER.info("{} {}", ctx.method(), ctx.path());
    next.handle(ctx);
  }
}
