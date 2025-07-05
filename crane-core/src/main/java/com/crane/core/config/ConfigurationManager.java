package com.crane.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {

  private static final String USER_CONFIG_PATH = "crane.yml";
  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  public static CraneConfig loadConfiguration() {
    try {
      // Load default configuration first
      CraneConfig config = loadDefaultConfig();

      // Try to load user configuration and merge
      CraneConfig userConfig = loadUserConfig();
      if (userConfig != null) {
        config = mergeConfigurations(config, userConfig);
      }

      // Validate required fields
      validateConfiguration(config);

      return config;
    } catch (Exception e) {
      throw new ConfigurationException("Failed to load configuration", e);
    }
  }

  private static CraneConfig loadDefaultConfig() {
    return new CraneConfig();
  }

  private static CraneConfig loadUserConfig() {
    try {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      InputStream is = contextClassLoader.getResourceAsStream(USER_CONFIG_PATH);
      if (is == null) {
        return null;
      }
      try (is) {
        return yamlMapper.readValue(is, CraneConfig.class);
      }
    } catch (Exception e) {
      throw new ConfigurationException("Failed to load user configuration from " + USER_CONFIG_PATH, e);
    }
  }

  private static CraneConfig mergeConfigurations(CraneConfig defaultConfig,
      CraneConfig userConfig) {
    if (userConfig.getServer() != null) {
      ServerConfig merged = new ServerConfig();
      merged.setPort(userConfig.getServer().getPort() != 0 ? userConfig.getServer().getPort() : defaultConfig.getServer().getPort());
      merged.setHost(userConfig.getServer().getHost() != null ? userConfig.getServer().getHost() : defaultConfig.getServer().getHost());
      defaultConfig.setServer(merged);
    }

    if (userConfig.getDatabase() != null) {
      defaultConfig.setDatabase(userConfig.getDatabase());
    }

    if (userConfig.getMail() != null) {
      defaultConfig.setMail(userConfig.getMail());
    }

    return defaultConfig;
  }

  private static void validateConfiguration(CraneConfig config) {
    List<String> errors = new ArrayList<>();

    // Validate server config
    if (config.getServer().getPort() <= 0 || config.getServer().getPort() > 65535) {
      errors.add("Server port must be between 1 and 65535");
    }

    if (!errors.isEmpty()) {
      throw new ConfigurationException(
          "Configuration validation failed: " + String.join(", ", errors));
    }
  }
}