package com.careauth.compass.domain.model;

import java.time.Instant;
import java.util.Objects;

public record AuditObservation(
        String referralId,
        AuditEventType eventType,
        String message,
        String policyRevisionId,
        Instant observedAt
) {
    public AuditObservation {
        Objects.requireNonNull(referralId, "referralId");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(policyRevisionId, "policyRevisionId");
        Objects.requireNonNull(observedAt, "observedAt");
    }
}
