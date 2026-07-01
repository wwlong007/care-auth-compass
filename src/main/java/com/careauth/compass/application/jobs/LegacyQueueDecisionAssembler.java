package com.careauth.compass.application.jobs;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import java.util.Comparator;

public class LegacyQueueDecisionAssembler {
    private final PolicyRevisionRepository policyRevisionRepository;

    public LegacyQueueDecisionAssembler(PolicyRevisionRepository policyRevisionRepository) {
        this.policyRevisionRepository = policyRevisionRepository;
    }

    public AuthorizationDecision assemble(Referral referral) {
        CoveragePolicyRevision revision = policyRevisionRepository
                .findByPayerPlanProcedure(referral.payerId(), referral.planCode(), referral.procedureCode())
                .stream()
                .filter(item -> item.isPublished())
                .filter(item -> item.isEffectiveOn(referral.serviceDate()))
                .min(Comparator.comparing(CoveragePolicyRevision::importedAt))
                .orElseThrow();
        return new AuthorizationDecision(referral.tenantId(), referral.id(), revision.outcome(),
                revision.queueName(), revision.id(), revision.requirements(), "LEGACY_QUEUE_ASSEMBLER");
    }
}
