package com.careauth.compass.domain.model;

import java.util.List;
import java.util.Objects;

public record AuthorizationDecision(
        String tenantId,
        String referralId,
        DecisionOutcome outcome,
        QueueName queueName,
        String policyRevisionId,
        List<ClinicalDocumentRequirement> requirements,
        String reasonCode
) {
    public AuthorizationDecision {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referralId, "referralId");
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(queueName, "queueName");
        Objects.requireNonNull(policyRevisionId, "policyRevisionId");
        requirements = List.copyOf(requirements);
        Objects.requireNonNull(reasonCode, "reasonCode");
    }
}
