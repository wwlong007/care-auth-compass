package com.careauth.compass.application.jobs;

import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.util.List;

public class RetroQueueRefreshPlanner {
    private final ReferralRepository referralRepository;

    public RetroQueueRefreshPlanner(ReferralRepository referralRepository) {
        this.referralRepository = referralRepository;
    }

    public List<Referral> planRefreshCandidates() {
        return referralRepository.findAll();
    }
}
