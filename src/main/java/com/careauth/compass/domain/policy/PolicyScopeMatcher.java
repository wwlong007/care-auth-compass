package com.careauth.compass.domain.policy;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;

public class PolicyScopeMatcher {
    public boolean isCandidateForDecision(CoveragePolicyRevision revision, AuthorizationRequest request) {
        PolicyScope requestScope = PolicyScope.from(request);
        PolicyScope revisionScope = PolicyScope.from(revision);
        return revisionScope.samePayerPlanProcedure(requestScope);
    }
}
