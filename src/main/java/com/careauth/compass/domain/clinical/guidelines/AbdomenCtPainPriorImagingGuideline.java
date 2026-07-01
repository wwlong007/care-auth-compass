package com.careauth.compass.domain.clinical.guidelines;

import com.careauth.compass.domain.model.QueueName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class AbdomenCtPainPriorImagingGuideline {
    private static final String PRIMARY_PROCEDURE = "CT-ABDOMEN";
    private static final String SPECIALTY = "gastroenterology";
    private static final QueueName DEFAULT_QUEUE = QueueName.CLINICAL_REVIEW;
    private static final List<String> REQUIRED_EVIDENCE = List.of("EXAM-NOTE", "LAB-RESULTS", "PRIOR-IMAGING", "REPORT-DATE", "COMPARISON-REQUEST");
    private static final Set<String> HARD_STOPS = Set.of("PERITONITIS", "DUPLICATE-REQUEST", "CONTRADICTORY-REPORT");

    public String primaryProcedure() {
        return PRIMARY_PROCEDURE;
    }

    public String specialty() {
        return SPECIALTY;
    }

    public boolean supports(String procedureCode, String requestedSpecialty) {
        return PRIMARY_PROCEDURE.equalsIgnoreCase(procedureCode)
                || SPECIALTY.equalsIgnoreCase(requestedSpecialty);
    }

    public QueueName defaultQueue() {
        return DEFAULT_QUEUE;
    }

    public List<String> requiredEvidence() {
        return REQUIRED_EVIDENCE;
    }

    public List<String> missingEvidence(Map<String, String> packet) {
        List<String> missing = new ArrayList<>();
        for (String requirement : REQUIRED_EVIDENCE) {
            if (!packet.containsKey(requirement) || packet.get(requirement).isBlank()) {
                missing.add(requirement);
            }
        }
        return List.copyOf(missing);
    }

    public boolean hasHardStop(Map<String, String> packet) {
        for (String blocker : HARD_STOPS) {
            String value = packet.getOrDefault(blocker, "");
            if ("true".equalsIgnoreCase(value) || "present".equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public int clinicalRiskScore(Map<String, String> packet, LocalDate serviceDate) {
        int score = 0;
        for (String requirement : REQUIRED_EVIDENCE) {
            if (packet.containsKey(requirement)) {
                score += 3;
            }
        }
        if (serviceDate != null && serviceDate.isBefore(LocalDate.of(2026, 1, 1))) {
            score += 2;
        }
        if (hasHardStop(packet)) {
            score += 25;
        }
        return score;
    }

    public Optional<String> firstActionableGap(Map<String, String> packet) {
        return missingEvidence(packet).stream().findFirst();
    }

    public Map<String, String> reviewerSummary(Map<String, String> packet) {
        Map<String, String> summary = new LinkedHashMap<>();
        summary.put("procedure", PRIMARY_PROCEDURE);
        summary.put("specialty", SPECIALTY);
        summary.put("queue", DEFAULT_QUEUE.name());
        summary.put("missingEvidenceCount", Integer.toString(missingEvidence(packet).size()));
        summary.put("hardStop", Boolean.toString(hasHardStop(packet)));
        for (String requirement : REQUIRED_EVIDENCE) {
            summary.put("evidence." + requirement.toLowerCase(Locale.ROOT), packet.getOrDefault(requirement, "missing"));
        }
        return Map.copyOf(summary);
    }

    public boolean mayAutoApprove(Map<String, String> packet, LocalDate serviceDate) {
        return missingEvidence(packet).isEmpty()
                && !hasHardStop(packet)
                && clinicalRiskScore(packet, serviceDate) < 20;
    }

    public String policyNarrative(Map<String, String> packet) {
        StringBuilder narrative = new StringBuilder();
        narrative.append(SPECIALTY).append(" review for ").append(PRIMARY_PROCEDURE).append(": ");
        if (missingEvidence(packet).isEmpty()) {
            narrative.append("packet is complete");
        } else {
            narrative.append("missing ").append(String.join(", ", missingEvidence(packet)));
        }
        if (hasHardStop(packet)) {
            narrative.append("; hard stop present");
        }
        return narrative.toString();
    }
}
