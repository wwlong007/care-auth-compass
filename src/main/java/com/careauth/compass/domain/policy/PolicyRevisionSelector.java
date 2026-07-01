package com.careauth.compass.domain.policy;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PolicyRevisionSelector {
    private final PolicyScopeMatcher scopeMatcher;

    public PolicyRevisionSelector(PolicyScopeMatcher scopeMatcher) {
        this.scopeMatcher = scopeMatcher;
    }

    public Optional<CoveragePolicyRevision> selectPublishedRevision(
            AuthorizationRequest request,
            List<CoveragePolicyRevision> revisions) {
        return revisions.stream()
                .filter(CoveragePolicyRevision::isPublished)
                .filter(revision -> scopeMatcher.isCandidateForDecision(revision, request))
                .max(Comparator.comparing(CoveragePolicyRevision::importedAt));
    }
}
