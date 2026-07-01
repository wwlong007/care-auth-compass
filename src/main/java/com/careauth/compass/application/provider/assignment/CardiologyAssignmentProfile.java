package com.careauth.compass.application.provider.assignment;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CardiologyAssignmentProfile {
    private static final String SPECIALTY = "cardiology";
    private static final String BOARD = "ABIM";
    private static final List<String> REQUIRED_ATTESTATIONS = List.of("license", "malpractice", "board-certification", "echo-certification");
    private static final Set<String> RESTRICTIONS = Set.of("structural-heart");

    public String specialty() {
        return SPECIALTY;
    }

    public String board() {
        return BOARD;
    }

    public List<String> requiredAttestations() {
        return REQUIRED_ATTESTATIONS;
    }

    public boolean hasRestriction(String restriction) {
        return RESTRICTIONS.contains(restriction);
    }

    public List<String> missingAttestations(Map<String, String> providerPacket) {
        List<String> missing = new ArrayList<>();
        for (String attestation : REQUIRED_ATTESTATIONS) {
            if (!providerPacket.containsKey(attestation)
                    || !"current".equalsIgnoreCase(providerPacket.get(attestation))) {
                missing.add(attestation);
            }
        }
        return List.copyOf(missing);
    }

    public boolean activeForReview(LocalDate credentialedAt, LocalDate serviceDate) {
        if (credentialedAt == null || serviceDate == null) {
            return false;
        }
        return Period.between(credentialedAt, serviceDate).getYears() < 3;
    }

    public int credentialingScore(Map<String, String> providerPacket, LocalDate credentialedAt, LocalDate serviceDate) {
        int score = 20;
        score -= missingAttestations(providerPacket).size() * 5;
        if (activeForReview(credentialedAt, serviceDate)) {
            score += 15;
        }
        if (!RESTRICTIONS.isEmpty()) {
            score -= RESTRICTIONS.size() * 3;
        }
        return score;
    }

    public Map<String, String> reviewAttributes(String providerId, Map<String, String> providerPacket) {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("providerId", providerId);
        attributes.put("specialty", SPECIALTY);
        attributes.put("board", BOARD);
        attributes.put("restrictionCount", Integer.toString(RESTRICTIONS.size()));
        attributes.put("missingAttestationCount", Integer.toString(missingAttestations(providerPacket).size()));
        attributes.put("attestations", String.join(",", REQUIRED_ATTESTATIONS));
        return Map.copyOf(attributes);
    }

    public boolean mayPerformProcedure(Map<String, String> providerPacket, String procedureFamily) {
        return missingAttestations(providerPacket).isEmpty()
                && !hasRestriction(procedureFamily)
                && providerPacket.getOrDefault("sanctions", "none").equalsIgnoreCase("none");
    }
}
