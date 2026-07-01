package com.careauth.compass.domain.policy;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.NetworkTier;
import com.careauth.compass.domain.model.ProductLine;
import com.careauth.compass.domain.model.SiteOfCare;

public record PolicyScope(
        String tenantId,
        String payerId,
        String planCode,
        ProductLine productLine,
        String procedureCode,
        NetworkTier networkTier,
        SiteOfCare siteOfCare
) {
    public static PolicyScope from(AuthorizationRequest request) {
        return new PolicyScope(
                request.tenantId(),
                request.payerId(),
                request.planCode(),
                request.productLine(),
                request.procedureCode(),
                request.networkTier(),
                request.siteOfCare());
    }

    public static PolicyScope from(CoveragePolicyRevision revision) {
        return new PolicyScope(
                revision.tenantId(),
                revision.payerId(),
                revision.planCode(),
                revision.productLine(),
                revision.procedureCode(),
                revision.networkTier(),
                revision.siteOfCare());
    }

    public boolean samePayerPlanProcedure(PolicyScope other) {
        return payerId.equals(other.payerId)
                && planCode.equals(other.planCode)
                && procedureCode.equals(other.procedureCode);
    }

    public boolean exactlyEquals(PolicyScope other) {
        return tenantId.equals(other.tenantId)
                && payerId.equals(other.payerId)
                && planCode.equals(other.planCode)
                && productLine == other.productLine
                && procedureCode.equals(other.procedureCode)
                && networkTier == other.networkTier
                && siteOfCare == other.siteOfCare;
    }
}
