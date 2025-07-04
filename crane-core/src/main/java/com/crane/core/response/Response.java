package com.crane.core.response;


import com.crane.core.enumaration.MessageEnum;
import com.crane.core.enumaration.ResponseEnum;

public class Response {

  public static <T> ResponsePayload<T> ok(T data) {
    return ResponsePayload.<T>builder()
        .data(data)
        .responseEnum(ResponseEnum.OK)
        .message(ResponseEnum.OK.getDescription())
        .success(ResponseEnum.OK.getIsSuccess())
        .showNotification(false)
        .build();
  }

  public static <T> ResponsePayload<T> notFound() {
    return ResponsePayload.<T>builder()
        .responseEnum(ResponseEnum.NOTFOUND)
        .message(MessageEnum.NOT_FOUND.getMessage())
        .success(ResponseEnum.NOTFOUND.getIsSuccess())
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> saveSuccess(T data) {
    return ResponsePayload.<T>builder()
        .responseEnum(ResponseEnum.OK)
        .message(MessageEnum.SAVE_SUCCESS.getMessage())
        .success(ResponseEnum.OK.getIsSuccess())
        .data(data)
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> recordExists() {
    return ResponsePayload.<T>builder()
        .responseEnum(ResponseEnum.WARNING)
        .message(MessageEnum.RECORD_EXISTS.getMessage())
        .success(ResponseEnum.WARNING.getIsSuccess())
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> updateSuccess(T data) {
    return ResponsePayload.<T>builder()
        .responseEnum(ResponseEnum.OK)
        .message(MessageEnum.UPDATE_SUCCESS.getMessage())
        .success(ResponseEnum.OK.getIsSuccess())
        .data(data)
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> deleteSuccess() {
    return ResponsePayload.<T>builder()
        .responseEnum(ResponseEnum.OK)
        .message(MessageEnum.DELETE_SUCCESS.getMessage())
        .success(ResponseEnum.OK.getIsSuccess())
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> set(ResponseEnum responseEnum) {
    return ResponsePayload.<T>builder()
        .responseEnum(responseEnum)
        .message(responseEnum.getDescription())
        .success(responseEnum.getIsSuccess())
        .showNotification(false)
        .build();
  }

  public static <T> ResponsePayload<T> set(T data, ResponseEnum responseEnum) {
    return ResponsePayload.<T>builder()
        .data(data)
        .responseEnum(responseEnum)
        .message(responseEnum.getDescription())
        .success(responseEnum.getIsSuccess())
        .showNotification(false)
        .build();
  }

  public static <T> ResponsePayload<T> set(ResponseEnum responseEnum, String message) {
    return ResponsePayload.<T>builder()
        .responseEnum(responseEnum)
        .message(message)
        .success(responseEnum.getIsSuccess())
        .showNotification(false)
        .build();
  }

  public static <T> ResponsePayload<T> set(T data, ResponseEnum responseEnum, String message) {
    return ResponsePayload.<T>builder()
        .data(data)
        .responseEnum(responseEnum)
        .message(message)
        .success(responseEnum.getIsSuccess())
        .showNotification(true)
        .build();
  }

  public static <T> ResponsePayload<T> set(T data, ResponseEnum responseEnum, String message,
      Boolean showNotification) {
    return ResponsePayload.<T>builder()
        .data(data)
        .responseEnum(responseEnum)
        .message(message)
        .success(responseEnum.getIsSuccess())
        .showNotification(showNotification)
        .build();
  }

}
