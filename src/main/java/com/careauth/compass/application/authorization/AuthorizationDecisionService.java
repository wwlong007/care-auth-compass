package com.careauth.compass.application.authorization;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;

public class AuthorizationDecisionService {
    private final CoveragePolicyResolver resolver;

    public AuthorizationDecisionService(CoveragePolicyResolver resolver) {
        this.resolver = resolver;
    }

    public AuthorizationDecision evaluate(AuthorizationRequest request) {
        CoveragePolicyRevision revision = resolver.resolve(request)
                .orElseThrow(() -> new IllegalStateException("No published coverage policy for " + request));
        return new AuthorizationDecision(
                request.tenantId(),
                request.referralId(),
                revision.outcome(),
                revision.queueName(),
                revision.id(),
                revision.requirements(),
                "POLICY_REVISION_" + revision.id());
    }
}
