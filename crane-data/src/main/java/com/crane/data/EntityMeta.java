package com.crane.data;

import com.crane.data.annotation.AutoGenerated;
import com.crane.data.annotation.AutoGenerated.Strategy;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityMeta<T> {

  private final Class<T> type;
  private final String table;
  private final String idField;
  private final List<String> columns;
  private final Map<String, Field> fieldMap;
  private final Set<String> autoGeneratedFields;

  public EntityMeta(Class<T> type, String table, String idField, List<String> columns,
      Map<String, Field> fieldMap, Set<String> autoGeneratedFields) {
    this.type = type;
    this.table = table;
    this.idField = idField;
    this.columns = columns;
    this.fieldMap = fieldMap;
    this.autoGeneratedFields = autoGeneratedFields;
  }

  public Class<T> type() {
    return type;
  }

  public String table() {
    return table;
  }

  public String idField() {
    return idField;
  }

  public List<String> columns() {
    return columns;
  }

  public Field getField(String columnName) {
    return fieldMap.get(columnName);
  }

  public boolean isAutoGenerated(String columnName) {
    return autoGeneratedFields.contains(columnName);
  }

  public Set<String> getAutoGeneratedFields() {
    return autoGeneratedFields;
  }

  public boolean isIdAutoGenerated() {
    return autoGeneratedFields.contains(idField);
  }

  /**
   * Check if field is generated by the database (exclude from INSERT)
   */
  public boolean isDatabaseGenerated(String columnName) {
    if (!isAutoGenerated(columnName)) {
      return false;
    }

    Field field = getField(columnName);
    if (field == null) {
      return false;
    }

    AutoGenerated annotation = field.getAnnotation(AutoGenerated.class);
    if (annotation == null) {
      return false;
    }

    AutoGenerated.Strategy strategy = annotation.strategy();
    return strategy == Strategy.DATABASE;
  }

  /**
   * Check if field is generated by application (include in INSERT after generation)
   */
  public boolean isApplicationGenerated(String columnName) {
    if (!isAutoGenerated(columnName)) {
      return false;
    }

    Field field = getField(columnName);
    if (field == null) {
      return false;
    }

    AutoGenerated annotation = field.getAnnotation(AutoGenerated.class);
    if (annotation == null) {
      return false;
    }

    AutoGenerated.Strategy strategy = annotation.strategy();
    return strategy == AutoGenerated.Strategy.UUID;
  }

  /**
   * Get fields that need database-generated keys returned
   */
  public Set<String> getDatabaseGeneratedFields() {
    return autoGeneratedFields.stream()
        .filter(this::isDatabaseGenerated)
        .collect(Collectors.toSet());
  }

  /**
   * Get fields that need application generation before insert
   */
  public Set<String> getApplicationGeneratedFields() {
    return autoGeneratedFields.stream()
        .filter(this::isApplicationGenerated)
        .collect(Collectors.toSet());
  }
}