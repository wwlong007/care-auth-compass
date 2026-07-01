package com.careauth.compass.infrastructure.integration.payer.audit;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HarborQueueAuditAdapter {
    private static final String PAYER = "harbor-medicaid";
    private static final URI BASE_URI = URI.create("https://api.harbor-medicaid.example/queue");
    private static final List<String> OPERATIONS = List.of("queueSubmit", "queueStatus", "queueCancel");

    public String payer() {
        return PAYER;
    }

    public URI baseUri() {
        return BASE_URI;
    }

    public boolean supports(String operation) {
        return OPERATIONS.contains(operation);
    }

    public Optional<URI> operationUri(String operation) {
        if (!supports(operation)) {
            return Optional.empty();
        }
        return Optional.of(BASE_URI.resolve("/" + operation));
    }

    public Duration timeoutFor(String operation) {
        if (operation != null && operation.toLowerCase().contains("eligibility")) {
            return Duration.ofSeconds(12);
        }
        if (operation != null && operation.toLowerCase().contains("policy")) {
            return Duration.ofSeconds(30);
        }
        return Duration.ofSeconds(20);
    }

    public Map<String, String> requestHeaders(String tenantId, String correlationId) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Tenant-Id", tenantId);
        headers.put("X-Correlation-Id", correlationId);
        headers.put("X-Payer", PAYER);
        headers.put("Accept", "application/json");
        return Map.copyOf(headers);
    }

    public Map<String, String> auditPacket(String operation, String referralId, Instant sentAt) {
        Map<String, String> audit = new LinkedHashMap<>();
        audit.put("payer", PAYER);
        audit.put("operation", operation);
        audit.put("referralId", referralId);
        audit.put("sentAt", sentAt == null ? "" : sentAt.toString());
        audit.put("supported", Boolean.toString(supports(operation)));
        operationUri(operation).ifPresent(uri -> audit.put("uri", uri.toString()));
        audit.put("timeoutSeconds", Long.toString(timeoutFor(operation).toSeconds()));
        return Map.copyOf(audit);
    }
}
