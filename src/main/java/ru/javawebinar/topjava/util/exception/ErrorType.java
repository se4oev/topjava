package ru.javawebinar.topjava.util.exception;

public enum ErrorType {
    APP_ERROR("App Error"),
    DATA_NOT_FOUND("Data not found"),
    DATA_ERROR("Data error"),
    VALIDATION_ERROR("Data validation error");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
