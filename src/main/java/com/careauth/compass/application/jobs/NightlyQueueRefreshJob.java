package com.careauth.compass.application.jobs;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.util.ArrayList;
import java.util.List;

public class NightlyQueueRefreshJob {
    private final ReferralRepository referralRepository;
    private final LegacyQueueDecisionAssembler legacyQueueDecisionAssembler;

    public NightlyQueueRefreshJob(ReferralRepository referralRepository,
                                  LegacyQueueDecisionAssembler legacyQueueDecisionAssembler) {
        this.referralRepository = referralRepository;
        this.legacyQueueDecisionAssembler = legacyQueueDecisionAssembler;
    }

    public List<AuthorizationDecision> refreshOpenQueues() {
        List<AuthorizationDecision> decisions = new ArrayList<>();
        for (Referral referral : referralRepository.findAll()) {
            AuthorizationDecision decision = legacyQueueDecisionAssembler.assemble(referral);
            referral.applyDecision(decision, false);
            referralRepository.save(referral);
            decisions.add(decision);
        }
        return decisions;
    }
}
