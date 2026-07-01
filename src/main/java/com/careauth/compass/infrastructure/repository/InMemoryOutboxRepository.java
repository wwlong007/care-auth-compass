package com.careauth.compass.infrastructure.repository;

import com.careauth.compass.domain.model.OutboxEvent;
import com.careauth.compass.domain.repository.OutboxRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemoryOutboxRepository implements OutboxRepository {
    private final List<OutboxEvent> events = new ArrayList<>();

    @Override
    public void publish(OutboxEvent event) {
        events.add(event);
    }

    @Override
    public List<OutboxEvent> findAll() {
        return List.copyOf(events);
    }
}
