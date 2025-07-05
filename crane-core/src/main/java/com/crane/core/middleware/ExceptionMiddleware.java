package com.crane.core.middleware;

import com.crane.core.Context;
import com.crane.core.Handler;
import com.crane.core.enumaration.ResponseEnum;
import com.crane.core.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionMiddleware implements Middleware {

  private static final Logger LOGGER = LogManager.getLogger(ExceptionMiddleware.class);


  @Override
  public void apply(Context ctx, Handler next) throws Exception {
    try {
      next.handle(ctx);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      ctx.errorResponse(Response.set(ResponseEnum.ERROR, e.getMessage()));
    }
  }
}
