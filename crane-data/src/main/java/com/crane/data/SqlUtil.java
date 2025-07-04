package com.crane.data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class SqlUtil {

  @SuppressWarnings("unchecked")
  protected static Object convertType(Object value, Class<?> targetType) {
    if (value == null || targetType.isAssignableFrom(value.getClass())) {
      return value;
    }

    // Handle common type conversions
    try {
      // String conversions
      if (targetType == String.class) {
        return value.toString();
      }

      // Number conversions
      if (value instanceof Number number) {
        if (targetType == Integer.class || targetType == int.class) {
          return number.intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
          return number.longValue();
        }
        if (targetType == Double.class || targetType == double.class) {
          return number.doubleValue();
        }
        if (targetType == Float.class || targetType == float.class) {
          return number.floatValue();
        }
        if (targetType == Short.class || targetType == short.class) {
          return number.shortValue();
        }
        if (targetType == Byte.class || targetType == byte.class) {
          return number.byteValue();
        }
        if (targetType == BigDecimal.class) {
          return new BigDecimal(number.toString());
        }
      }

      // String to Number conversions
      if (value instanceof String str && !str.isEmpty()) {
        if (targetType == Integer.class || targetType == int.class) {
          return Integer.valueOf(str);
        }
        if (targetType == Long.class || targetType == long.class) {
          return Long.valueOf(str);
        }
        if (targetType == Double.class || targetType == double.class) {
          return Double.valueOf(str);
        }
        if (targetType == Float.class || targetType == float.class) {
          return Float.valueOf(str);
        }
        if (targetType == BigDecimal.class) {
          return new BigDecimal(str);
        }
      }

      // Boolean conversions
      if (targetType == Boolean.class || targetType == boolean.class) {
        if (value instanceof Number number) {
          return number.intValue() != 0;
        }
        if (value instanceof String str) {
          return Boolean.parseBoolean(str) || "1".equals(str) || "Y".equalsIgnoreCase(str);
        }
      }

      // Date/Time conversions
      if (targetType == java.util.Date.class && value instanceof java.sql.Timestamp) {
        return new java.util.Date(((java.sql.Timestamp) value).getTime());
      }
      if (targetType == java.sql.Timestamp.class && value instanceof java.util.Date) {
        return new java.sql.Timestamp(((java.util.Date) value).getTime());
      }
      if (targetType == LocalDateTime.class && value instanceof java.sql.Timestamp) {
        return ((java.sql.Timestamp) value).toLocalDateTime();
      }
      if (targetType == LocalDate.class && value instanceof java.sql.Date) {
        return ((java.sql.Date) value).toLocalDate();
      }
      if (targetType == LocalTime.class && value instanceof java.sql.Time) {
        return ((java.sql.Time) value).toLocalTime();
      }

      // Enum conversions
      if (targetType.isEnum()) {
        if (value instanceof String) {
          return Enum.valueOf((Class<Enum>) targetType, (String) value);
        }
        if (value instanceof Number) {
          Object[] enumConstants = targetType.getEnumConstants();
          int ordinal = ((Number) value).intValue();
          if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
          }
        }
      }

      // UUID conversions
      if (targetType == UUID.class && value instanceof String) {
        return UUID.fromString((String) value);
      }
      if (targetType == String.class && value instanceof UUID) {
        return value.toString();
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to convert " + value.getClass().getSimpleName() +
          " to " + targetType.getSimpleName() + ": " + e.getMessage(), e);
    }

    // If no conversion found, throw exception
    throw new RuntimeException("No conversion available from " + value.getClass().getSimpleName() +
        " to " + targetType.getSimpleName());
  }

  protected static int getSqlType(Class<?> javaType) {
    if (javaType == String.class) {
      return Types.VARCHAR;
    }
    if (javaType == Integer.class || javaType == int.class) {
      return Types.INTEGER;
    }
    if (javaType == Long.class || javaType == long.class) {
      return Types.BIGINT;
    }
    if (javaType == Double.class || javaType == double.class) {
      return Types.DOUBLE;
    }
    if (javaType == Float.class || javaType == float.class) {
      return Types.FLOAT;
    }
    if (javaType == Boolean.class || javaType == boolean.class) {
      return Types.BOOLEAN;
    }
    if (javaType == BigDecimal.class) {
      return Types.DECIMAL;
    }
    if (javaType == java.util.Date.class || javaType == java.sql.Timestamp.class) {
      return Types.TIMESTAMP;
    }
    if (javaType == java.sql.Date.class || javaType == LocalDate.class) {
      return Types.DATE;
    }
    if (javaType == java.sql.Time.class || javaType == LocalTime.class) {
      return Types.TIME;
    }
    if (javaType == LocalDateTime.class) {
      return Types.TIMESTAMP;
    }
    if (javaType == UUID.class) {
      return Types.VARCHAR;
    }
    if (javaType.isEnum()) {
      return Types.VARCHAR;
    }

    return Types.OTHER; // Default fallback
  }

  /**
   * Set parameters for PreparedStatement from ordered list
   */
  protected static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
    for (int i = 0; i < parameters.size(); i++) {
      Object value = parameters.get(i);
      if (value != null) {
        stmt.setObject(i + 1, value);
      } else {
        // Handle null values - you might want to improve this based on your SqlUtil
        stmt.setNull(i + 1, java.sql.Types.NULL);
      }
    }
  }


  /**
   * Map ResultSet to DTO using reflection Supports both field-based and constructor-based mapping
   */
  protected static <T> T mapRowToDto(ResultSet rs, Class<T> dtoClass) throws SQLException {
    try {
      // Try constructor-based mapping first (for records or DTOs with constructor)
      T instance = tryConstructorMapping(rs, dtoClass);
      if (instance != null) {
        return instance;
      }

      // Fall back to field-based mapping
      return tryFieldMapping(rs, dtoClass);

    } catch (Exception e) {
      throw new SQLException("Failed to map ResultSet to DTO: " + dtoClass.getSimpleName(), e);
    }
  }

  /**
   * Try constructor-based mapping (works well with records)
   */
  private static <T> T tryConstructorMapping(ResultSet rs, Class<T> dtoClass) {
    try {
      // Get ResultSet metadata to know available columns
      var metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      // Get constructors and try to find one that matches column count
      var constructors = dtoClass.getDeclaredConstructors();

      for (var constructor : constructors) {
        if (constructor.getParameterCount() == columnCount) {
          Object[] args = new Object[columnCount];
          Class<?>[] paramTypes = constructor.getParameterTypes();

          for (int i = 0; i < columnCount; i++) {
            String columnName = metaData.getColumnName(i + 1);
            Object value = rs.getObject(columnName);

            if (value != null) {
              // Convert type if necessary
              args[i] = SqlUtil.convertType(value, paramTypes[i]);
            } else {
              args[i] = null;
            }
          }

          constructor.setAccessible(true);
          return (T) constructor.newInstance(args);
        }
      }
    } catch (Exception e) {
      // Constructor mapping failed, will try field mapping
    }
    return null;
  }

  /**
   * Try field-based mapping
   */
  private static <T> T tryFieldMapping(ResultSet rs, Class<T> dtoClass) throws Exception {
    T instance = dtoClass.getDeclaredConstructor().newInstance();
    var metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();

    for (int i = 1; i <= columnCount; i++) {
      String columnName = metaData.getColumnName(i);
      Object value = rs.getObject(columnName);

      if (value != null) {
        // Try to find field with matching name (case insensitive)
        Field targetField = findFieldByName(dtoClass, columnName);
        if (targetField != null) {
          targetField.setAccessible(true);
          Object convertedValue = SqlUtil.convertType(value, targetField.getType());
          targetField.set(instance, convertedValue);
        }
      }
    }

    return instance;
  }

  /**
   * Find field by name (case insensitive, handles snake_case to camelCase conversion)
   */
  private static Field findFieldByName(Class<?> clazz, String columnName) {
    // Try exact match first
    try {
      return clazz.getDeclaredField(columnName);
    } catch (NoSuchFieldException e) {
      // Try camelCase conversion (snake_case -> camelCase)
      String camelCase = toCamelCase(columnName);
      try {
        return clazz.getDeclaredField(camelCase);
      } catch (NoSuchFieldException ex) {
        // Try case insensitive search
        for (Field field : clazz.getDeclaredFields()) {
          if (field.getName().equalsIgnoreCase(columnName) ||
              field.getName().equalsIgnoreCase(camelCase)) {
            return field;
          }
        }
      }
    }
    return null;
  }

  /**
   * Convert snake_case to camelCase
   */
  private static String toCamelCase(String snakeCase) {
    if (snakeCase == null || snakeCase.isEmpty()) {
      return snakeCase;
    }

    StringBuilder camelCase = new StringBuilder();
    boolean capitalizeNext = false;

    for (char c : snakeCase.toCharArray()) {
      if (c == '_') {
        capitalizeNext = true;
      } else {
        if (capitalizeNext) {
          camelCase.append(Character.toUpperCase(c));
          capitalizeNext = false;
        } else {
          camelCase.append(Character.toLowerCase(c));
        }
      }
    }

    return camelCase.toString();
  }




}
