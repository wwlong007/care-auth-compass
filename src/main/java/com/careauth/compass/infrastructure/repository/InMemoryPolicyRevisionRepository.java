package com.careauth.compass.infrastructure.repository;

import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemoryPolicyRevisionRepository implements PolicyRevisionRepository {
    private final List<CoveragePolicyRevision> revisions = new ArrayList<>();

    public InMemoryPolicyRevisionRepository(List<CoveragePolicyRevision> revisions) {
        this.revisions.addAll(revisions);
    }

    @Override
    public List<CoveragePolicyRevision> findAll() {
        return List.copyOf(revisions);
    }
}
