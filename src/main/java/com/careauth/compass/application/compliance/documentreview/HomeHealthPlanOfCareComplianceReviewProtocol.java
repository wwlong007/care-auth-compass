package com.careauth.compass.application.compliance.documentreview;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class HomeHealthPlanOfCareComplianceReviewProtocol {
    private static final String DOCUMENT_FAMILY = "home-health-plan-of-care";
    private static final String TRIGGER = "HOME-HEALTH";
    private static final List<String> DOCUMENT_CODES = List.of("FACE-TO-FACE", "PLAN-OF-CARE", "HOMEBOUND-STATUS", "SKILL-NEED", "CAREGIVER-NOTE");
    private static final Set<String> OPTIONAL_CODES = Set.of("SKILL-NEED", "CAREGIVER-NOTE");

    public String documentFamily() {
        return DOCUMENT_FAMILY;
    }

    public String trigger() {
        return TRIGGER;
    }

    public List<String> documentCodes() {
        return DOCUMENT_CODES;
    }

    public boolean isOptional(String documentCode) {
        return OPTIONAL_CODES.contains(documentCode);
    }

    public List<String> requiredDocuments() {
        List<String> required = new ArrayList<>();
        for (String code : DOCUMENT_CODES) {
            if (!isOptional(code)) {
                required.add(code);
            }
        }
        return List.copyOf(required);
    }

    public Map<String, String> defaultStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        for (String code : DOCUMENT_CODES) {
            statuses.put(code, isOptional(code) ? "OPTIONAL" : "MISSING");
        }
        return Map.copyOf(statuses);
    }

    public List<String> missingRequired(Map<String, String> statuses) {
        List<String> missing = new ArrayList<>();
        for (String code : requiredDocuments()) {
            String status = statuses.getOrDefault(code, "MISSING");
            if (!"RECEIVED".equalsIgnoreCase(status) && !"WAIVED".equalsIgnoreCase(status)) {
                missing.add(code);
            }
        }
        return List.copyOf(missing);
    }

    public boolean readyForClinicalReview(Map<String, String> statuses, LocalDate serviceDate) {
        return missingRequired(statuses).isEmpty()
                && serviceDate != null
                && !serviceDate.isBefore(LocalDate.of(2026, 1, 1));
    }

    public String checklistLabel(String code) {
        String normalized = code.replace('-', ' ').toLowerCase(Locale.ROOT);
        return DOCUMENT_FAMILY + " - " + normalized;
    }

    public Map<String, String> displayLabels() {
        Map<String, String> labels = new LinkedHashMap<>();
        for (String code : DOCUMENT_CODES) {
            labels.put(code, checklistLabel(code));
        }
        return Map.copyOf(labels);
    }
}
