package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.IBaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

public class ConcurentHashMapStorage<Value extends IBaseEntity> implements Storage<Integer, Value> {

    private static final Logger log = getLogger(ConcurentHashMapStorage.class);

    private final Map<Integer, Value> storage = new ConcurrentHashMap<>();
    private final Supplier<Integer> idSupplier;

    public ConcurentHashMapStorage(Supplier<Integer> idSupplier) {
        this.idSupplier = idSupplier;
    }

    @Override
    public Value findById(Integer id) {
        log.info("find by id: {}", id);
        Value value = storage.get(id);
        if (value == null)
            throw new RuntimeException("Entity not found with id: " + id);
        return value;
    }

    @Override
    public Integer save(Value value) {
        Integer id = value.getId();
        if (id == null || id == 0) {
            Integer nextId = idSupplier.get();
            log.info("create new entity, id: {}", nextId);
            value.setId(nextId);
            id = nextId;
        }
        log.info("save entity, id: {}", id);
        storage.put(id, value);
        return id;
    }

    @Override
    public void deleteById(Integer id) {
        Value removed = storage.remove(id);
        if (removed == null)
            throw new RuntimeException("Entity not found for id: " + id);

    }

    @Override
    public List<Value> findAll() {
        return new ArrayList<>(storage.values());
    }

}
