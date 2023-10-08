package ru.javawebinar.topjava.util;

public interface Sequence<Id> {

    Id nextId(Class<?> clazz);
}
