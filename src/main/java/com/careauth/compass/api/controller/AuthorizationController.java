package com.careauth.compass.api.controller;

import com.careauth.compass.api.dto.AuthorizationEvaluateRequest;
import com.careauth.compass.api.dto.AuthorizationEvaluateResponse;
import com.careauth.compass.application.authorization.AuthorizationDecisionService;
import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.AuthorizationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authorizations")
public class AuthorizationController {
    private final AuthorizationDecisionService decisionService;

    public AuthorizationController(AuthorizationDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/evaluate")
    public AuthorizationEvaluateResponse evaluate(@RequestBody AuthorizationEvaluateRequest body) {
        AuthorizationDecision decision = decisionService.evaluate(new AuthorizationRequest(
                body.tenantId(), body.payerId(), body.planCode(), body.productLine(), body.memberId(),
                body.referralId(), body.procedureCode(), body.networkTier(), body.siteOfCare(), body.serviceDate()));
        return new AuthorizationEvaluateResponse(decision.referralId(), decision.outcome(), decision.queueName(),
                decision.policyRevisionId(), decision.requirements(), decision.reasonCode());
    }
}
