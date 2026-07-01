package com.careauth.compass.application.jobs;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.util.ArrayList;
import java.util.List;

public class NightlyQueueRefreshJob {
    private final RetroQueueRefreshPlanner refreshPlanner;
    private final LegacyQueueDecisionAssembler legacyQueueDecisionAssembler;

    public NightlyQueueRefreshJob(ReferralRepository referralRepository,
                                  LegacyQueueDecisionAssembler legacyQueueDecisionAssembler) {
        this.refreshPlanner = new RetroQueueRefreshPlanner(referralRepository);
        this.legacyQueueDecisionAssembler = legacyQueueDecisionAssembler;
    }

    public List<AuthorizationDecision> refreshOpenQueues() {
        List<AuthorizationDecision> decisions = new ArrayList<>();
        for (Referral referral : refreshPlanner.planRefreshCandidates()) {
            AuthorizationDecision decision = legacyQueueDecisionAssembler.assemble(referral);
            referral.applyDecision(decision, false);
            decisions.add(decision);
        }
        return decisions;
    }
}
