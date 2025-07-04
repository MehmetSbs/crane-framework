package com.crane.core;

import com.crane.core.config.ConfigurationManager;
import com.crane.core.config.CraneConfig;
import com.crane.core.config.DatabaseConfig;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

  private final Router router = new Router();
  private final AppContext appContext = new AppContext();
  private final List<Middleware> middlewareList = new ArrayList<>();

  private DatabaseConfig dbConfig;
  private boolean dataModuleAvailable = false;

  private static final Logger LOGGER = LogManager.getLogger(Server.class);

  public Server() {
    LOGGER.info("CRANE FRAMEWORK V1.0.0");
  }

  public void start() throws Exception {

    long start = System.nanoTime();

    CraneConfig craneConfig = ConfigurationManager.loadConfiguration();
    if (craneConfig.getDatabase() != null) {
      dbConfig = craneConfig.getDatabase();
    }



    LOGGER.info("Looking for database configuration");

    // Check if data module is available and initialize if needed
    if (dbConfig != null && isDataModuleAvailable()) {
      initializeDataSource();
      LOGGER.info("Database connection pool initialized");
    } else if (dbConfig != null && !isDataModuleAvailable()) {
      LOGGER.warn("DatabaseConfig provided but crane-data module not found in classpath");
    } else {
      LOGGER.warn("DatabaseConfig not provided — DB layer will not work");
    }

    HttpServer httpServer = HttpServer.create(new InetSocketAddress(craneConfig.getServer().getPort()), 0);
    LOGGER.info("Http Server Created");

    use(new LogMiddleware());
    use(new ExceptionMiddleware());

    httpServer.createContext("/", exchange -> {
      var handler = router.route(exchange.getRequestMethod(), exchange.getRequestURI().getPath());
      if (handler != null) {
        Thread.startVirtualThread(() -> {
          try {
            Context context = new Context(exchange);
            Handler finalHandler = handler;

            for (int i = middlewareList.size() - 1; i >= 0; i--) {
              Middleware middleware = middlewareList.get(i);
              Handler next = finalHandler;
              finalHandler = ctx -> middleware.apply(ctx, next);
            }

            finalHandler.handle(context);
          } catch (Exception e) {
            try {
              exchange.sendResponseHeaders(500, 0);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
            try {
              exchange.getResponseBody().close();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
            LOGGER.error(e);
          }
        });
      } else {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().close();
      }
    });
    httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    httpServer.start();
    long time = System.nanoTime() - start;
    LOGGER.info("Server started at http://{}:{} {}", craneConfig.getServer().getHost(), craneConfig.getServer().getPort(),
            "in " + (double) time / 1_000_000_000L + " seconds");
  }

  private boolean isDataModuleAvailable() {
    if (dataModuleAvailable) {
      return true;
    }

    try {
      Class.forName("com.crane.data.DataSourceProvider");
      dataModuleAvailable = true;
      LOGGER.info("crane-data module detected");
      return true;
    } catch (ClassNotFoundException e) {
      LOGGER.debug("crane-data module not found in classpath");
      return false;
    }
  }

  private void initializeDataSource() {
    try {
      // Use reflection to call DataSourceProvider.init()
      Class<?> dataSourceProviderClass = Class.forName("com.crane.data.DataSourceProvider");

      // Get the fields from DatabaseConfig
      String jdbcUrl = dbConfig.getJdbcUrl();
      String username = dbConfig.getUsername();
      String password = dbConfig.getPassword();

      // Call DataSourceProvider.init(jdbcUrl, username, password)
      var initMethod = dataSourceProviderClass.getMethod("init", String.class, String.class, String.class);
      initMethod.invoke(null, jdbcUrl, username, password);

    } catch (Exception e) {
      LOGGER.error("Failed to initialize data source", e);
      throw new RuntimeException("Failed to initialize data source", e);
    }
  }

  public void use(Middleware middleware) {
    middlewareList.add(middleware);
  }

  public <T> void registerService(Class<T> type, T instance) {
    appContext.register(type, instance);
  }

  public void registerController(Controller controller) {
    controller.register(router, appContext);
  }


  public void setDatabaseConfig(String jdbcUrl, String username, String password) {
    if (isDataModuleAvailable()) {
      try {

          this.dbConfig = new DatabaseConfig(jdbcUrl, username, password);
      } catch (Exception e) {
        LOGGER.error("Failed to create DatabaseConfig", e);
        throw new RuntimeException("Failed to create DatabaseConfig", e);
      }
    } else {
      LOGGER.warn("Cannot set database config - crane-data module not available");
    }
  }
}
