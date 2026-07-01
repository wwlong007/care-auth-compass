package com.careauth.compass.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ReferralDecisionSnapshot(
        String id,
        String referralId,
        String policyRevisionId,
        DecisionOutcome outcome,
        QueueName queueName,
        boolean snapshotLocked,
        Instant decidedAt,
        List<ClinicalDocumentRequirement> requirements
) {
    public ReferralDecisionSnapshot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(referralId, "referralId");
        Objects.requireNonNull(policyRevisionId, "policyRevisionId");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(queueName, "queueName");
        Objects.requireNonNull(decidedAt, "decidedAt");
        requirements = List.copyOf(requirements);
    }
}
