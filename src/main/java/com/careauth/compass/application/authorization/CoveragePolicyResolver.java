package com.careauth.compass.application.authorization;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.infrastructure.cache.PolicyRuleCache;
import java.util.Optional;

public class CoveragePolicyResolver {
    private final PolicyRuleCache policyRuleCache;

    public CoveragePolicyResolver(PolicyRuleCache policyRuleCache) {
        this.policyRuleCache = policyRuleCache;
    }

    public Optional<CoveragePolicyRevision> resolve(AuthorizationRequest request) {
        return policyRuleCache.resolve(request);
    }
}
