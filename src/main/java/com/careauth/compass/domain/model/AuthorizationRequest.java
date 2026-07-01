package com.careauth.compass.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public record AuthorizationRequest(
        String tenantId,
        String payerId,
        String planCode,
        ProductLine productLine,
        String memberId,
        String referralId,
        String procedureCode,
        NetworkTier networkTier,
        SiteOfCare siteOfCare,
        LocalDate serviceDate
) {
    public AuthorizationRequest {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(payerId, "payerId");
        Objects.requireNonNull(planCode, "planCode");
        Objects.requireNonNull(productLine, "productLine");
        Objects.requireNonNull(memberId, "memberId");
        Objects.requireNonNull(referralId, "referralId");
        Objects.requireNonNull(procedureCode, "procedureCode");
        Objects.requireNonNull(networkTier, "networkTier");
        Objects.requireNonNull(siteOfCare, "siteOfCare");
        Objects.requireNonNull(serviceDate, "serviceDate");
    }
}
