package com.careauth.compass.domain.policy;

import com.careauth.compass.domain.model.AuthorizationRequest;

public record PolicySnapshotKey(
        String payerId,
        String planCode,
        String procedureCode
) {
    public static PolicySnapshotKey of(AuthorizationRequest request) {
        return new PolicySnapshotKey(request.payerId(), request.planCode(), request.procedureCode());
    }
}
