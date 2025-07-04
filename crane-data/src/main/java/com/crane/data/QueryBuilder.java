package com.crane.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryBuilder {

  private String sql;
  private final Map<String, Object> namedParams = new HashMap<>();
  private Class<?> resultType;
  private final JdbcRepository<?, ?> repository;

  // Pattern to match named parameters like :paramName
  private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile(":([a-zA-Z_][a-zA-Z0-9_]*)");

  QueryBuilder(JdbcRepository<?, ?> repository) {
    this.repository = repository;
  }

  /**
   * Set the SQL query string with named parameters
   *
   * @param sql SQL query with named parameters (e.g., "SELECT * FROM users WHERE id = :userId")
   * @return QueryBuilder for method chaining
   */
  public QueryBuilder sql(String sql) {
    this.sql = sql;
    return this;
  }

  /**
   * Add a named parameter to the query
   *
   * @param name  Parameter name (without the colon)
   * @param value Parameter value
   * @return QueryBuilder for method chaining
   */
  public QueryBuilder param(String name, Object value) {
    namedParams.put(name, value);
    return this;
  }

  /**
   * Specify the result type for mapping
   *
   * @param resultType Class to map results to
   * @return QueryBuilder for method chaining
   */
  public <T> QueryBuilder mapTo(Class<T> resultType) {
    this.resultType = resultType;
    return this;
  }

  /**
   * Execute query and return list of results
   *
   * @return List of mapped objects
   * @throws SQLException if query execution fails
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> list() throws SQLException {
    validateQuery();
    return repository.executeCustomQuery(sql, namedParams, (Class<T>) resultType);
  }

  /**
   * Execute query and return single result
   *
   * @return Optional containing single result or empty if no results
   * @throws SQLException if query execution fails or multiple results returned
   */
  public <T> Optional<T> single() throws SQLException {
    List<T> results = list();
    if (results.isEmpty()) {
      return Optional.empty();
    }
    if (results.size() > 1) {
      throw new SQLException(
          "Query returned " + results.size() + " results when single result expected");
    }
    return Optional.of(results.get(0));
  }

  /**
   * Execute query and return single result, throw exception if no result
   *
   * @return Single mapped object
   * @throws SQLException if query execution fails, no results, or multiple results returned
   */
  public <T> T singleOrThrow() throws SQLException {
    return this.<T>single().orElseThrow(() ->
        new SQLException("Query returned no results when single result expected"));
  }

  /**
   * Execute query for count/aggregate operations
   *
   * @return Long value (typically used for COUNT queries)
   * @throws SQLException if query execution fails
   */
  public Long count() throws SQLException {
    validateQuery();
    return repository.executeCountQuery(sql, namedParams);
  }

  /**
   * Execute update/delete query
   *
   * @return Number of affected rows
   * @throws SQLException if query execution fails
   */
  public int execute() throws SQLException {
    validateQuery();
    return repository.executeUpdateQuery(sql, namedParams);
  }

  private void validateQuery() {
    if (sql == null || sql.trim().isEmpty()) {
      throw new IllegalStateException("SQL query cannot be null or empty");
    }
    if (resultType == null && !sql.trim().toUpperCase().startsWith("SELECT")) {
      // For non-SELECT queries, resultType is not required
      return;
    }

    // Validate that all named parameters in SQL have corresponding values
    Matcher matcher = NAMED_PARAM_PATTERN.matcher(sql);
    while (matcher.find()) {
      String paramName = matcher.group(1);
      if (!namedParams.containsKey(paramName)) {
        throw new IllegalStateException("Missing parameter value for: " + paramName);
      }
    }
  }

  /**
   * Internal method to parse SQL and replace named parameters with question marks Also returns the
   * ordered list of parameter values
   */
  static class ParsedQuery {

    final String sql;
    final List<Object> parameters;

    ParsedQuery(String sql, List<Object> parameters) {
      this.sql = sql;
      this.parameters = parameters;
    }
  }

  static ParsedQuery parseNamedParameters(String sql, Map<String, Object> namedParams) {
    List<Object> orderedParams = new ArrayList<>();
    StringBuffer processedSql = new StringBuffer();

    Matcher matcher = NAMED_PARAM_PATTERN.matcher(sql);
    while (matcher.find()) {
      String paramName = matcher.group(1);
      Object paramValue = namedParams.get(paramName);
      if (paramValue == null && !namedParams.containsKey(paramName)) {
        throw new IllegalArgumentException("No value provided for parameter: " + paramName);
      }
      orderedParams.add(paramValue);
      matcher.appendReplacement(processedSql, "?");
    }
    matcher.appendTail(processedSql);

    return new ParsedQuery(processedSql.toString(), orderedParams);
  }
}