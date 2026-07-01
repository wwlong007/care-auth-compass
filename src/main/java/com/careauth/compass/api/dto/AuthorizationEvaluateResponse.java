package com.careauth.compass.api.dto;

import com.careauth.compass.domain.model.ClinicalDocumentRequirement;
import com.careauth.compass.domain.model.DecisionOutcome;
import com.careauth.compass.domain.model.QueueName;
import java.util.List;

public record AuthorizationEvaluateResponse(
        String referralId,
        DecisionOutcome outcome,
        QueueName queueName,
        String policyRevisionId,
        List<ClinicalDocumentRequirement> requirements,
        String reasonCode
) {
}
