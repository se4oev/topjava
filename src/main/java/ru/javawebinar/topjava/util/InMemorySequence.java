package ru.javawebinar.topjava.util;

import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySequence implements Sequence<Integer> {

    private final AtomicInteger id = new AtomicInteger(0);

    @Override
    public Integer nextId() {
        return id.incrementAndGet();
    }
}
