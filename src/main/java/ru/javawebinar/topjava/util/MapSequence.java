package ru.javawebinar.topjava.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapSequence implements Sequence<Integer> {

    private static final Map<Class<?>, AtomicInteger> sequenceMap = new ConcurrentHashMap<>();

    @Override
    public Integer nextId(Class<?> clazz) {
        AtomicInteger atomicInteger = sequenceMap
                .computeIfAbsent(clazz, aClass -> new AtomicInteger(0));
        return atomicInteger.incrementAndGet();
    }

}
