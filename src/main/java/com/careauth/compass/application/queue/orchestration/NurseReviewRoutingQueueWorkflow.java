package com.careauth.compass.application.queue.orchestration;

import com.careauth.compass.domain.model.QueueName;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NurseReviewRoutingQueueWorkflow {
    private static final String BUSINESS_OWNER = "clinical-operations";
    private static final QueueName QUEUE = QueueName.NURSE_REVIEW;
    private static final List<String> STAGES = List.of("ready", "assigned", "reviewed", "resolved");
    private static final Map<String, String> TRANSITIONS = Map.ofEntries(
            Map.entry("ready", "assigned"),
            Map.entry("assigned", "reviewed"),
            Map.entry("reviewed", "resolved")
    );

    public String businessOwner() {
        return BUSINESS_OWNER;
    }

    public QueueName queue() {
        return QUEUE;
    }

    public List<String> stages() {
        return STAGES;
    }

    public Optional<String> nextStage(String currentStage) {
        return Optional.ofNullable(TRANSITIONS.get(currentStage));
    }

    public boolean canAdvance(String currentStage, Map<String, String> facts) {
        if (!TRANSITIONS.containsKey(currentStage)) {
            return false;
        }
        if (facts.containsKey("manualHold") && "true".equalsIgnoreCase(facts.get("manualHold"))) {
            return false;
        }
        return facts.containsKey("tenantId") && facts.containsKey("referralId");
    }

    public Duration serviceLevelFor(String stage) {
        int position = STAGES.indexOf(stage);
        if (position < 0) {
            return Duration.ofHours(4);
        }
        return Duration.ofMinutes(45L + (position * 30L));
    }

    public Map<String, String> workItemAttributes(String referralId, String stage, Instant now) {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("referralId", referralId);
        attributes.put("stage", stage);
        attributes.put("owner", BUSINESS_OWNER);
        attributes.put("queue", QUEUE.name());
        attributes.put("createdAt", now == null ? "" : now.toString());
        attributes.put("slaMinutes", Long.toString(serviceLevelFor(stage).toMinutes()));
        nextStage(stage).ifPresent(next -> attributes.put("nextStage", next));
        return Map.copyOf(attributes);
    }

    public List<String> blockedReasons(Map<String, String> facts) {
        List<String> blocked = new ArrayList<>();
        if (!facts.containsKey("tenantId")) {
            blocked.add("tenantId");
        }
        if (!facts.containsKey("referralId")) {
            blocked.add("referralId");
        }
        if ("true".equalsIgnoreCase(facts.getOrDefault("manualHold", "false"))) {
            blocked.add("manualHold");
        }
        return List.copyOf(blocked);
    }

    public boolean shouldEscalate(String stage, Instant createdAt, Instant now) {
        if (createdAt == null || now == null) {
            return false;
        }
        return Duration.between(createdAt, now).compareTo(serviceLevelFor(stage)) > 0;
    }

    public String routingMemo(String referralId, String stage, Map<String, String> facts) {
        if (blockedReasons(facts).isEmpty()) {
            return referralId + " can advance from " + stage + " in " + QUEUE.name();
        }
        return referralId + " blocked at " + stage + " by " + String.join(",", blockedReasons(facts));
    }
}
