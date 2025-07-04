package com.crane.core.response;

import com.crane.core.enumaration.ResponseEnum;

public class ResponsePayload<T> {

  private Integer code;
  private String message;
  private Boolean success;
  private ResponseEnum responseEnum;
  private Boolean showNotification;
  private T data;

  public ResponsePayload(Integer code, String message, Boolean success, ResponseEnum responseEnum,
      Boolean showNotification, T data) {
    this.code = code;
    this.message = message;
    this.success = success;
    this.responseEnum = responseEnum;
    this.showNotification = showNotification;
    this.data = data;
  }

  public static <T> ResponsePayloadBuilder<T> builder() {
    return new ResponsePayloadBuilder<>();
  }

  public static class ResponsePayloadBuilder<T> {

    private Integer code;
    private String message;
    private Boolean success;
    private ResponseEnum responseEnum;
    private Boolean showNotification;
    private T data;


    public ResponsePayloadBuilder<T> message(String message) {
      this.message = message;
      return this;
    }

    public ResponsePayloadBuilder<T> success(Boolean success) {
      this.success = success;
      return this;
    }

    public ResponsePayloadBuilder<T> responseEnum(ResponseEnum responseEnum) {
      this.responseEnum = responseEnum;
      this.code = responseEnum.getHttpStatusCode();
      return this;
    }

    public ResponsePayloadBuilder<T> showNotification(Boolean showNotification) {
      this.showNotification = showNotification;
      return this;
    }

    public ResponsePayloadBuilder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ResponsePayload<T> build() {
      return new ResponsePayload<>(
          this.code,
          this.message,
          this.success,
          this.responseEnum,
          this.showNotification,
          this.data
      );
    }
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public ResponseEnum getResponseEnum() {
    return responseEnum;
  }

  public void setResponseEnum(ResponseEnum responseEnum) {
    this.responseEnum = responseEnum;
  }

  public Boolean getShowNotification() {
    return showNotification;
  }

  public void setShowNotification(Boolean showNotification) {
    this.showNotification = showNotification;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
