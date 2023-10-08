package ru.javawebinar.topjava.storage;

import java.util.List;

public interface Storage<Id, Value> {

    Value findById(Id id);

    Value save(Value value);

    void deleteById(Id id);

    List<Value> findAll();
}
