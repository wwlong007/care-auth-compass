package com.careauth.compass.api.controller;

import com.careauth.compass.api.dto.AuthorizationEvaluateResponse;
import com.careauth.compass.application.referral.ReferralReevaluationService;
import com.careauth.compass.domain.model.AuthorizationDecision;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {
    private final ReferralReevaluationService reevaluationService;

    public ReferralController(ReferralReevaluationService reevaluationService) {
        this.reevaluationService = reevaluationService;
    }

    @PostMapping("/{id}/refresh-decision")
    public AuthorizationEvaluateResponse refreshDecision(@PathVariable String id) {
        AuthorizationDecision decision = reevaluationService.refreshDecision(id);
        return new AuthorizationEvaluateResponse(decision.referralId(), decision.outcome(), decision.queueName(),
                decision.policyRevisionId(), decision.requirements(), decision.reasonCode());
    }
}
