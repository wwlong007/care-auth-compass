package com.careauth.compass.application.referral;

import com.careauth.compass.application.authorization.AuthorizationDecisionService;
import com.careauth.compass.application.authorization.ChecklistMaterializer;
import com.careauth.compass.domain.model.AuditEventType;
import com.careauth.compass.domain.model.AuditObservation;
import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.OutboxEvent;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.AuditRepository;
import com.careauth.compass.domain.repository.OutboxRepository;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.time.Instant;

public class ReferralReevaluationService {
    private final ReferralRepository referralRepository;
    private final PolicyRevisionRepository policyRevisionRepository;
    private final AuthorizationDecisionService decisionService;
    private final ChecklistMaterializer checklistMaterializer;
    private final AuditRepository auditRepository;
    private final OutboxRepository outboxRepository;

    public ReferralReevaluationService(ReferralRepository referralRepository,
                                       PolicyRevisionRepository policyRevisionRepository,
                                       AuthorizationDecisionService decisionService,
                                       ChecklistMaterializer checklistMaterializer,
                                       AuditRepository auditRepository,
                                       OutboxRepository outboxRepository) {
        this.referralRepository = referralRepository;
        this.policyRevisionRepository = policyRevisionRepository;
        this.decisionService = decisionService;
        this.checklistMaterializer = checklistMaterializer;
        this.auditRepository = auditRepository;
        this.outboxRepository = outboxRepository;
    }

    public AuthorizationDecision refreshDecision(String referralId) {
        Referral referral = referralRepository.require(referralId);
        AuthorizationDecision decision = decisionService.evaluate(referral.toAuthorizationRequest());
        CoveragePolicyRevision revision = policyRevisionRepository.findAll().stream()
                .filter(item -> item.id().equals(decision.policyRevisionId()))
                .findFirst()
                .orElseThrow();
        checklistMaterializer.materialize(referral, revision);
        referral.applyDecision(decision, false);
        referralRepository.save(referral);
        auditRepository.record(new AuditObservation(referral.id(), AuditEventType.DECISION_REFRESHED,
                "Referral decision refreshed from policy " + decision.policyRevisionId(),
                decision.policyRevisionId(), Instant.parse("2026-06-15T08:31:00Z")));
        outboxRepository.publish(new OutboxEvent(referral.id(), "ReferralDecisionRefreshed",
                decision.policyRevisionId(), Instant.parse("2026-06-15T08:31:00Z")));
        return decision;
    }
}
