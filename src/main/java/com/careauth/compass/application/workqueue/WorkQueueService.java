package com.careauth.compass.application.workqueue;

import com.careauth.compass.domain.model.QueueName;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.util.List;

public class WorkQueueService {
    private final ReferralRepository referralRepository;

    public WorkQueueService(ReferralRepository referralRepository) {
        this.referralRepository = referralRepository;
    }

    public List<WorkQueueItem> findItems(QueueName queueName) {
        return referralRepository.findAll().stream()
                .filter(referral -> referral.decisionSnapshot().queueName() == queueName)
                .map(this::toItem)
                .toList();
    }

    private WorkQueueItem toItem(Referral referral) {
        return new WorkQueueItem(referral.id(), referral.decisionSnapshot().queueName(),
                referral.decisionSnapshot().policyRevisionId(), referral.status().name());
    }
}
