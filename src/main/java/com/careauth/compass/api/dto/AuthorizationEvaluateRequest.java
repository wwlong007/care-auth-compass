package com.careauth.compass.api.dto;

import com.careauth.compass.domain.model.NetworkTier;
import com.careauth.compass.domain.model.ProductLine;
import com.careauth.compass.domain.model.SiteOfCare;
import java.time.LocalDate;

public record AuthorizationEvaluateRequest(
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
}
