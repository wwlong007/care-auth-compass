package com.careauth.compass.domain.model;

import java.time.Instant;
import java.util.Objects;

public record OutboxEvent(
        String aggregateId,
        String eventType,
        String payload,
        Instant createdAt
) {
    public OutboxEvent {
        Objects.requireNonNull(aggregateId, "aggregateId");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(payload, "payload");
        Objects.requireNonNull(createdAt, "createdAt");
    }
}
