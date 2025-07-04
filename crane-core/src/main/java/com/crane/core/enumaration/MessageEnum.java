package com.crane.core.enumaration;


public enum MessageEnum {

    NOT_FOUND(1, "The record is not found!"),
    NOT_AUTH(2, "You are not authorized for this process!"),
    UNEXPECTED_ERROR(3, "UNEXPECTED ERROR!"),
    UPDATE_SUCCESS(4,"Record is updated successfully."),
    SAVE_SUCCESS(5,"Record is saved successfully."),
    DELETE_SUCCESS(6,"Record is deleted successfully."),
    RECORD_EXISTS(7,"Record already exists");

    private final Integer code;
    private final String message;

    MessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

}
