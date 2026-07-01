package com.careauth.compass.infrastructure.cache;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.policy.PolicySnapshotKey;

public class PolicyCacheKeyFactory {
    public PolicySnapshotKey keyFor(AuthorizationRequest request) {
        return PolicySnapshotKey.of(request);
    }
}
