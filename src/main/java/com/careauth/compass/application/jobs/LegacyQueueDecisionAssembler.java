package com.careauth.compass.application.jobs;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.policy.PolicyRevisionSelector;
import com.careauth.compass.domain.policy.PolicyScopeMatcher;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;

public class LegacyQueueDecisionAssembler {
    private final PolicyRevisionRepository policyRevisionRepository;
    private final PolicyRevisionSelector policyRevisionSelector;

    public LegacyQueueDecisionAssembler(PolicyRevisionRepository policyRevisionRepository) {
        this.policyRevisionRepository = policyRevisionRepository;
        this.policyRevisionSelector = new PolicyRevisionSelector(new PolicyScopeMatcher());
    }

    public AuthorizationDecision assemble(Referral referral) {
        CoveragePolicyRevision revision = policyRevisionSelector
                .selectPublishedRevision(referral.toAuthorizationRequest(), policyRevisionRepository.findAll())
                .orElseThrow();
        return new AuthorizationDecision(referral.tenantId(), referral.id(), revision.outcome(),
                revision.queueName(), revision.id(), revision.requirements(), "LEGACY_QUEUE_ASSEMBLER");
    }
}
