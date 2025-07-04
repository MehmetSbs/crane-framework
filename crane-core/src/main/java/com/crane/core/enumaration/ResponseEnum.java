package com.crane.core.enumaration;


public enum ResponseEnum {
  OK(200, "OK", Boolean.TRUE),
  BADREQUEST(400, "BADREQUEST", Boolean.FALSE),
  UNAUTHORIZED(401, "UNAUTHORIZED", Boolean.FALSE),
  FORBIDDEN(403, "FORBIDDEN", Boolean.FALSE),
  NOTFOUND(404, "NOTFOUND", Boolean.FALSE),
  ERROR(500, "ERROR", Boolean.FALSE),
  NOTIFICATION(1001, "NOTIFICATION", Boolean.FALSE),
  INFO(1002, "INFO", Boolean.TRUE),
  WARNING(100, "WARNING", Boolean.TRUE);

  private final Integer httpStatusCode;
  private final String description;
  private final Boolean isSuccess;

  private ResponseEnum(Integer httpStatusCode, String description, Boolean isSuccess) {
    this.httpStatusCode = httpStatusCode;
    this.description = description;
    this.isSuccess = isSuccess;
  }

  public Integer getHttpStatusCode() {
    return httpStatusCode;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getIsSuccess() {
    return isSuccess;
  }
}