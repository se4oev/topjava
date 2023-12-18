package ru.javawebinar.topjava.util.exception;

import java.util.List;

public class IllegalFieldsException extends RuntimeException {
    private final List<String> fieldsErrors;

    public IllegalFieldsException(List<String> fieldsErrors) {
        this.fieldsErrors = fieldsErrors;
    }

    public List<String> getFieldsErrors() {
        return fieldsErrors;
    }
}
