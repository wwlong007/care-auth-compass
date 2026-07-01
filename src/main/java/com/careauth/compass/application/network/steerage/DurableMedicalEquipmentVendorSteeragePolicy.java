package com.careauth.compass.application.network.steerage;

import com.careauth.compass.domain.model.NetworkTier;
import com.careauth.compass.domain.model.QueueName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class DurableMedicalEquipmentVendorSteeragePolicy {
    private static final String FACILITY_TYPE = "dme-vendor";
    private static final NetworkTier CONTRACTED_TIER = NetworkTier.IN_NETWORK;
    private static final List<String> CAPABILITIES = List.of("wheelchair", "oxygen", "hospital-bed");
    private static final Set<String> EXCLUSIONS = Set.of("CUSTOM-PROSTHETIC", "INVESTIGATIONAL-DME");

    public String facilityType() {
        return FACILITY_TYPE;
    }

    public NetworkTier contractedTier() {
        return CONTRACTED_TIER;
    }

    public List<String> capabilities() {
        return CAPABILITIES;
    }

    public boolean supportsCapability(String capability) {
        return CAPABILITIES.stream().anyMatch(item -> item.equalsIgnoreCase(capability));
    }

    public boolean excludedProcedure(String procedureCode) {
        return EXCLUSIONS.contains(procedureCode);
    }

    public QueueName routingQueueFor(String procedureCode, boolean memberContinuityOfCare) {
        if (excludedProcedure(procedureCode)) {
            return QueueName.CLINICAL_REVIEW;
        }
        if (CONTRACTED_TIER == NetworkTier.OUT_OF_NETWORK && !memberContinuityOfCare) {
            return QueueName.PAYER_FOLLOW_UP;
        }
        return QueueName.NONE;
    }

    public Optional<String> firstMissingCapability(List<String> requestedCapabilities) {
        for (String requested : requestedCapabilities) {
            if (!supportsCapability(requested)) {
                return Optional.of(requested);
            }
        }
        return Optional.empty();
    }

    public int steerageScore(List<String> requestedCapabilities, LocalDate serviceDate) {
        int score = CONTRACTED_TIER == NetworkTier.IN_NETWORK ? 30 : 5;
        for (String capability : requestedCapabilities) {
            if (supportsCapability(capability)) {
                score += 6;
            } else {
                score -= 8;
            }
        }
        if (serviceDate != null && serviceDate.getYear() >= 2026) {
            score += 4;
        }
        return score;
    }

    public Map<String, String> contractAttributes(String facilityId, String procedureCode, LocalDate serviceDate) {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("facilityId", facilityId);
        attributes.put("facilityType", FACILITY_TYPE);
        attributes.put("networkTier", CONTRACTED_TIER.name());
        attributes.put("procedureCode", procedureCode);
        attributes.put("serviceDate", serviceDate == null ? "" : serviceDate.toString());
        attributes.put("excluded", Boolean.toString(excludedProcedure(procedureCode)));
        attributes.put("capabilities", String.join(",", CAPABILITIES));
        return Map.copyOf(attributes);
    }

    public List<String> steerageMessages(List<String> requestedCapabilities, String procedureCode) {
        List<String> messages = new ArrayList<>();
        if (excludedProcedure(procedureCode)) {
            messages.add("Procedure requires contract exception review");
        }
        firstMissingCapability(requestedCapabilities).ifPresent(capability ->
                messages.add("Facility missing capability " + capability.toLowerCase(Locale.ROOT)));
        if (messages.isEmpty()) {
            messages.add("Facility contract supports requested site of care");
        }
        return List.copyOf(messages);
    }
}
