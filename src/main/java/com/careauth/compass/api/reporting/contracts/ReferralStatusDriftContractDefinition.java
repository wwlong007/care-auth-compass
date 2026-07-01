package com.careauth.compass.api.reporting.contracts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ReferralStatusDriftContractDefinition {
    private static final String REPORT_NAME = "Referral Status Drift";
    private static final String OWNER = "compliance";
    private static final List<String> METRICS = List.of("snapshotChanges", "lockedAttempts", "auditOnly");
    private static final List<String> DIMENSIONS = List.of("status", "queue", "policyRevision");

    public String reportName() {
        return REPORT_NAME;
    }

    public String owner() {
        return OWNER;
    }

    public List<String> metrics() {
        return METRICS;
    }

    public List<String> dimensions() {
        return DIMENSIONS;
    }

    public boolean supportsMetric(String metric) {
        return METRICS.contains(metric);
    }

    public Optional<String> primaryDimension() {
        return DIMENSIONS.stream().findFirst();
    }

    public Map<String, String> exportMetadata(LocalDate from, LocalDate through) {
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("reportName", REPORT_NAME);
        metadata.put("owner", OWNER);
        metadata.put("from", from == null ? "" : from.toString());
        metadata.put("through", through == null ? "" : through.toString());
        metadata.put("metrics", String.join(",", METRICS));
        metadata.put("dimensions", String.join(",", DIMENSIONS));
        return Map.copyOf(metadata);
    }

    public BigDecimal rate(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> summarize(Map<String, Long> counters) {
        Map<String, BigDecimal> summary = new LinkedHashMap<>();
        long total = counters.values().stream().mapToLong(Long::longValue).sum();
        for (String metric : METRICS) {
            long value = counters.getOrDefault(metric, 0L);
            summary.put(metric, rate(value, total));
        }
        return Map.copyOf(summary);
    }

    public String dashboardTitle(LocalDate through) {
        return REPORT_NAME + " as of " + (through == null ? "current period" : through);
    }
}
