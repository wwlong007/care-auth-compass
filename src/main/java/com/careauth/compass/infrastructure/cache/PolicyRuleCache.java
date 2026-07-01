package com.careauth.compass.infrastructure.cache;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.policy.PolicyRevisionSelector;
import com.careauth.compass.domain.policy.PolicyScopeMatcher;
import com.careauth.compass.domain.policy.PolicySnapshotKey;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Optional;

public class PolicyRuleCache {
    private final PolicyRevisionRepository repository;
    private final PolicyRevisionSelector selector;
    private final PolicyCacheKeyFactory keyFactory;
    private final Cache<PolicySnapshotKey, CoveragePolicyRevision> cache;

    public PolicyRuleCache(PolicyRevisionRepository repository) {
        this.repository = repository;
        this.selector = new PolicyRevisionSelector(new PolicyScopeMatcher());
        this.keyFactory = new PolicyCacheKeyFactory();
        this.cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(15))
                .build();
    }

    public Optional<CoveragePolicyRevision> resolve(AuthorizationRequest request) {
        PolicySnapshotKey key = keyFactory.keyFor(request);
        CoveragePolicyRevision revision = cache.get(key, ignored ->
                selector.selectPublishedRevision(request, repository.findAll()).orElse(null));
        return Optional.ofNullable(revision);
    }

    public long estimatedSize() {
        return cache.estimatedSize();
    }
}
