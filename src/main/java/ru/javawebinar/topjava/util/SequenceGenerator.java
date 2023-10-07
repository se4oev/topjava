package ru.javawebinar.topjava.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SequenceGenerator {

    private static final Map<Class<?>, Integer> sequenceMap = new ConcurrentHashMap<>();

    public static Integer nextId(Class<?> clazz) {
        synchronized (clazz) {
            Integer currentId = sequenceMap.get(clazz);
            if (currentId == null)
                currentId = 0;
            else
                currentId++;
            sequenceMap.put(clazz, currentId);
            return currentId;
        }
    }

}
