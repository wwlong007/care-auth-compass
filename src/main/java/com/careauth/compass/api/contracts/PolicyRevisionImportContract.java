package com.careauth.compass.api.contracts;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PolicyRevisionImportContract {
    private static final String RESOURCE = "policy-revision-import";
    private static final List<String> REQUIRED_FIELDS = List.of("tenantId", "payerId", "planCode", "revisionId", "effectiveFrom");
    private static final Set<String> EVENT_TYPES = Set.of("imported", "published");

    public String resource() {
        return RESOURCE;
    }

    public List<String> requiredFields() {
        return REQUIRED_FIELDS;
    }

    public boolean supportsEvent(String eventType) {
        return EVENT_TYPES.contains(eventType);
    }

    public List<String> missingFields(Map<String, String> payload) {
        List<String> missing = new ArrayList<>();
        for (String field : REQUIRED_FIELDS) {
            if (!payload.containsKey(field) || payload.get(field).isBlank()) {
                missing.add(field);
            }
        }
        return List.copyOf(missing);
    }

    public boolean validForIngestion(Map<String, String> payload, String eventType) {
        return supportsEvent(eventType) && missingFields(payload).isEmpty();
    }

    public Map<String, String> responseEnvelope(String correlationId, String eventType, Instant receivedAt) {
        Map<String, String> envelope = new LinkedHashMap<>();
        envelope.put("resource", RESOURCE);
        envelope.put("correlationId", correlationId);
        envelope.put("eventType", eventType);
        envelope.put("receivedAt", receivedAt == null ? "" : receivedAt.toString());
        envelope.put("supported", Boolean.toString(supportsEvent(eventType)));
        return Map.copyOf(envelope);
    }

    public Map<String, String> redact(Map<String, String> payload) {
        Map<String, String> redacted = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            if (entry.getKey().toLowerCase().contains("member")
                    || entry.getKey().toLowerCase().contains("diagnosis")) {
                redacted.put(entry.getKey(), "***");
            } else {
                redacted.put(entry.getKey(), entry.getValue());
            }
        }
        return Map.copyOf(redacted);
    }
}
