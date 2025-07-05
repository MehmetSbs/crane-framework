package com.crane.core;

public class RouteInfo {
    private final Handler handler;
    private final boolean transactional;

    public RouteInfo(Handler handler, boolean transactional) {
        this.handler = handler;
        this.transactional = transactional;
    }

    public Handler getHandler() { return handler; }
    public boolean isTransactional() { return transactional; }
}
