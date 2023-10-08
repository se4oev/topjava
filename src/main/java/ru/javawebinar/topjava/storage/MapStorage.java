package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.BaseEntity;
import ru.javawebinar.topjava.util.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class MapStorage<Value extends BaseEntity> implements Storage<Integer, Value> {

    private static final Logger log = getLogger(MapStorage.class);

    private final Map<Integer, Value> storage = new ConcurrentHashMap<>();
    private final Sequence<Integer> sequence;

    public MapStorage(Sequence<Integer> sequence) {
        this.sequence = sequence;
    }

    @Override
    public Value findById(Integer id) {
        log.info("find by id: {}", id);
        return storage.get(id);
    }

    @Override
    public Value save(Value value) {
        Integer id = value.getId();
        if (id == null) {
            Integer nextId = sequence.nextId(value.getClass());
            log.info("create new entity, id: {}", nextId);
            value.setId(nextId);
            id = nextId;
        } else {
            Value oldValue = findById(value.getId());
            if (oldValue == null) {
                log.warn("entity not found for update, id: {}", value.getId());
                return null;
            }
        }
        log.info("save entity, id: {}", id);
        return storage.put(id, value);
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
