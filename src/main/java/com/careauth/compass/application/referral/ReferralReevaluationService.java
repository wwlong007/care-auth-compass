package com.careauth.compass.application.referral;

import com.careauth.compass.application.authorization.AuthorizationDecisionService;
import com.careauth.compass.application.authorization.ChecklistMaterializer;
import com.careauth.compass.domain.model.AuditEventType;
import com.careauth.compass.domain.model.AuditObservation;
import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.AuditRepository;
import com.careauth.compass.domain.repository.OutboxRepository;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.careauth.compass.domain.repository.ReferralRepository;

public class ReferralReevaluationService {
    private final ReferralRepository referralRepository;
    private final PolicyRevisionRepository policyRevisionRepository;
    private final AuthorizationDecisionService decisionService;
    private final ChecklistMaterializer checklistMaterializer;
    private final ReferralLifecyclePolicy lifecyclePolicy;
    private final ReferralAuditPublisher auditPublisher;

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
        this.lifecyclePolicy = new ReferralLifecyclePolicy();
        this.auditPublisher = new ReferralAuditPublisher(auditRepository, outboxRepository);
    }

    public AuthorizationDecision refreshDecision(String referralId) {
        Referral referral = referralRepository.require(referralId);
        AuthorizationDecision decision = decisionService.evaluate(referral.toAuthorizationRequest());
        if (!lifecyclePolicy.mayOverwriteRetroSnapshot(referral)) {
            auditPublisher.lockedSnapshotObserved(referral, decision.policyRevisionId());
            return decision;
        }
        CoveragePolicyRevision revision = policyRevisionRepository.findAll().stream()
                .filter(item -> item.id().equals(decision.policyRevisionId()))
                .findFirst()
                .orElseThrow();
        checklistMaterializer.materialize(referral, revision);
        referral.applyDecision(decision, false);
        referralRepository.save(referral);
        auditPublisher.decisionRefreshed(referral, decision.policyRevisionId());
        return decision;
    }
}
