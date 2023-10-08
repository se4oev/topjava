package ru.javawebinar.topjava.storage;

import java.util.List;

public interface Storage<Id, Value> {

    Value findById(Id id);

    Id save(Value value);

    void deleteById(Id id);

    List<Value> findAll();

}
