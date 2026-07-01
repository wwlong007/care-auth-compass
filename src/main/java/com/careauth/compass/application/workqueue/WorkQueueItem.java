package com.careauth.compass.application.workqueue;

import com.careauth.compass.domain.model.QueueName;
import java.util.Objects;

public record WorkQueueItem(
        String referralId,
        QueueName queueName,
        String policyRevisionId,
        String status
) {
    public WorkQueueItem {
        Objects.requireNonNull(referralId, "referralId");
        Objects.requireNonNull(queueName, "queueName");
        Objects.requireNonNull(policyRevisionId, "policyRevisionId");
        Objects.requireNonNull(status, "status");
    }
}
