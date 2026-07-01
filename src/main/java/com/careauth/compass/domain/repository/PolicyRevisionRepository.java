package com.careauth.compass.domain.repository;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.PolicyStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface PolicyRevisionRepository {
    List<CoveragePolicyRevision> findAll();

    default List<CoveragePolicyRevision> findByPayerPlanProcedure(String payerId, String planCode, String procedureCode) {
        return findAll().stream()
                .filter(revision -> revision.payerId().equals(payerId))
                .filter(revision -> revision.planCode().equals(planCode))
                .filter(revision -> revision.procedureCode().equals(procedureCode))
                .toList();
    }

    default Optional<CoveragePolicyRevision> findLatestPublishedFor(AuthorizationRequest request) {
        return findByPayerPlanProcedure(request.payerId(), request.planCode(), request.procedureCode()).stream()
                .filter(revision -> revision.status() == PolicyStatus.PUBLISHED)
                .max(Comparator.comparing(CoveragePolicyRevision::importedAt));
    }

    default Optional<CoveragePolicyRevision> findEffectiveFor(AuthorizationRequest request) {
        return findAll().stream()
                .filter(CoveragePolicyRevision::isPublished)
                .filter(revision -> revision.matchesExactly(request))
                .filter(revision -> revision.isEffectiveOn(request.serviceDate()))
                .max(Comparator.comparing(CoveragePolicyRevision::specificityScore)
                        .thenComparing(CoveragePolicyRevision::effectiveFrom)
                        .thenComparing(CoveragePolicyRevision::importedAt));
    }
}
