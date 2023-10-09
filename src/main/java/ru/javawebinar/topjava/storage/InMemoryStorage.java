package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.BaseEntity;
import ru.javawebinar.topjava.util.InMemorySequence;
import ru.javawebinar.topjava.util.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryStorage<Value extends BaseEntity> implements Storage<Integer, Value> {

    private static final Logger log = getLogger(InMemoryStorage.class);

    private final Map<Integer, Value> storage = new ConcurrentHashMap<>();
    private final Sequence<Integer> sequence = new InMemorySequence();

    @Override
    public Value findById(Integer id) {
        log.info("find by id: {}", id);
        return storage.get(id);
    }

    @Override
    public Value save(Value value) {
        Integer id = value.getId();
        if (id == null) {
            return doSave(value);
        }
        return doUpdate(id, value);
    }

    private Value doSave(Value value) {
        Integer nextId = sequence.nextId();
        log.info("create new entity, id: {}", nextId);
        value.setId(nextId);
        storage.put(nextId, value);
        return value;
    }

    private synchronized Value doUpdate(Integer id, Value value) {
        Value oldValue = findById(value.getId());
        if (oldValue == null) {
            log.warn("entity not found for update, id: {}", value.getId());
            return null;
        }
        log.info("save entity, id: {}", id);
        return storage.put(id, value);
    }

    @Override
    public synchronized void deleteById(Integer id) {
        storage.remove(id);
    }

    @Override
    public List<Value> findAll() {
        return new ArrayList<>(storage.values());
    }
}
