package com.crane.core;

import com.crane.core.config.ConfigurationManager;
import com.crane.core.config.CraneConfig;
import com.crane.core.config.DatabaseConfig;
import com.crane.core.config.MailConfig;
import com.crane.core.middleware.ExceptionMiddleware;
import com.crane.core.middleware.LogMiddleware;
import com.crane.core.middleware.Middleware;
import com.crane.core.middleware.TransactionalMiddleware;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Server {

    private final Router router = new Router();
    private final AppContext appContext = new AppContext();
    private final List<Middleware> middlewareList = new ArrayList<>();

    private CraneConfig craneConfig;
    private DatabaseConfig dbConfig;
    private MailConfig mailConfig;
    private boolean dataModuleAvailable = false;
    private boolean mailModuleAvailable = false;

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public Server() {
        LOGGER.info("CRANE FRAMEWORK V1.0.0");

        craneConfig = ConfigurationManager.loadConfiguration();

        if (craneConfig.getDatabase() != null) {
            dbConfig = craneConfig.getDatabase();
        }
        if (craneConfig.getMail() != null) {
            mailConfig = craneConfig.getMail();
        }

        if (mailConfig != null && isMailModuleAvailable()) {
            registerJavaMailSender(mailConfig);
            LOGGER.info("JavaMailSender has been registered");
        } else if (mailConfig != null && !isMailModuleAvailable()) {
            LOGGER.warn("MailConfig provided but crane-mail module not found in classpath");
        }

    }

    public void start() throws Exception {

        long start = System.nanoTime();


        use(new LogMiddleware());
        use(new ExceptionMiddleware());

        LOGGER.info("Looking for database configuration");

        if (dbConfig != null && isDataModuleAvailable()) {
            DataSource dataSource = initializeDataSource();
            LOGGER.info("Database connection pool initialized");
            use(new TransactionalMiddleware(dataSource));
            LOGGER.info("TransactionalMiddleware has been enabled.");
        } else if (dbConfig != null && !isDataModuleAvailable()) {
            LOGGER.warn("DatabaseConfig provided but crane-data module not found in classpath");
        } else {
            LOGGER.warn("DatabaseConfig not provided — DB layer will not work");
        }


        HttpServer httpServer = HttpServer.create(new InetSocketAddress(craneConfig.getServer().getPort()), 0);
        LOGGER.info("Http Server Created");


        httpServer.createContext("/", exchange -> {
            var routeInfo = router.route(exchange.getRequestMethod(), exchange.getRequestURI().getPath());
            if (routeInfo != null) {
                Thread.startVirtualThread(() -> {
                    try {
                        Context context = new Context(exchange);
                        if (routeInfo.isTransactional()) {
                            context.markTransactional();
                        }
                        Handler finalHandler = routeInfo.getHandler();

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

    private boolean isMailModuleAvailable() {
        if (mailModuleAvailable) {
            return true;
        }
        try {
            Class.forName("com.crane.mail.sender.JavaMailSender");
            mailModuleAvailable = true;
            LOGGER.info("crane-mail module detected");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
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

    private DataSource initializeDataSource() {
        try {
            // Use reflection to call DataSourceProvider.init()
            Class<?> dataSourceProviderClass = Class.forName("com.crane.data.DataSourceProvider");

            // Get the fields from DatabaseConfig
            String jdbcUrl = dbConfig.getJdbcUrl();
            String username = dbConfig.getUsername();
            String password = dbConfig.getPassword();

            // Call DataSourceProvider.init(jdbcUrl, username, password)
            var initMethod = dataSourceProviderClass.getMethod("init", String.class, String.class, String.class);
            return (DataSource) initMethod.invoke(null, jdbcUrl, username, password);

        } catch (Exception e) {
            LOGGER.error("Failed to initialize data source", e);
            throw new RuntimeException("Failed to initialize data source", e);
        }
    }


    private void registerJavaMailSender(MailConfig mailConfig) {
        try {
            Class<?> javaMailSenderClass = Class.forName("com.crane.mail.sender.JavaMailSender");
            Constructor<?> constructor = javaMailSenderClass.getConstructor(MailConfig.class);
            Object instance = constructor.newInstance(mailConfig);

            appContext.registerInstance((Class<Object>) javaMailSenderClass, instance);

        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to register Java Mail Sender", e);
            throw new RuntimeException("Failed to register Java Mail Sender", e);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void use(Middleware middleware) {
        middlewareList.add(middleware);
    }

    public <T> void registerComponent(Class<T> type) {
        appContext.register(type);
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
