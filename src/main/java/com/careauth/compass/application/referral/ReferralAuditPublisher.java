package com.careauth.compass.application.referral;

import com.careauth.compass.domain.model.AuditEventType;
import com.careauth.compass.domain.model.AuditObservation;
import com.careauth.compass.domain.model.OutboxEvent;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.AuditRepository;
import com.careauth.compass.domain.repository.OutboxRepository;
import java.time.Instant;

public class ReferralAuditPublisher {
    private static final Instant SCENARIO_CLOCK = Instant.parse("2026-06-15T08:31:00Z");

    private final AuditRepository auditRepository;
    private final OutboxRepository outboxRepository;

    public ReferralAuditPublisher(AuditRepository auditRepository, OutboxRepository outboxRepository) {
        this.auditRepository = auditRepository;
        this.outboxRepository = outboxRepository;
    }

    public void decisionRefreshed(Referral referral, String policyRevisionId) {
        auditRepository.record(new AuditObservation(referral.id(), AuditEventType.DECISION_REFRESHED,
                "Referral decision refreshed from policy " + policyRevisionId,
                policyRevisionId, SCENARIO_CLOCK));
        outboxRepository.publish(new OutboxEvent(referral.id(), "ReferralDecisionRefreshed",
                policyRevisionId, SCENARIO_CLOCK));
    }

    public void lockedSnapshotObserved(Referral referral, String policyRevisionId) {
        auditRepository.record(new AuditObservation(referral.id(), AuditEventType.LOCKED_SNAPSHOT_OBSERVED,
                "Retro policy " + policyRevisionId + " observed without overwriting locked snapshot",
                policyRevisionId, SCENARIO_CLOCK));
    }
}
