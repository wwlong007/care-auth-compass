package com.careauth.compass.domain.coverage.benefits;

import com.careauth.compass.domain.model.NetworkTier;
import com.careauth.compass.domain.model.ProductLine;
import com.careauth.compass.domain.model.QueueName;
import com.careauth.compass.domain.model.SiteOfCare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MedicaidManagedCareDmeBenefitPolicy {
    private static final ProductLine PRODUCT_LINE = ProductLine.MEDICAID_MANAGED_CARE;
    private static final NetworkTier NETWORK_TIER = NetworkTier.IN_NETWORK;
    private static final SiteOfCare SITE_OF_CARE = SiteOfCare.OFFICE;
    private static final String BENEFIT_FAMILY = "durable-medical-equipment";
    private static final Set<String> EXCLUDED_MODIFIERS = Set.of("RR", "NU");

    public ProductLine productLine() {
        return PRODUCT_LINE;
    }

    public NetworkTier networkTier() {
        return NETWORK_TIER;
    }

    public SiteOfCare siteOfCare() {
        return SITE_OF_CARE;
    }

    public String benefitFamily() {
        return BENEFIT_FAMILY;
    }

    public boolean appliesTo(ProductLine productLine, NetworkTier networkTier, SiteOfCare siteOfCare) {
        return PRODUCT_LINE == productLine
                && NETWORK_TIER == networkTier
                && SITE_OF_CARE == siteOfCare;
    }

    public boolean excludesModifier(String modifier) {
        return EXCLUDED_MODIFIERS.contains(modifier);
    }

    public QueueName defaultExceptionQueue(String modifier) {
        if (modifier != null && excludesModifier(modifier)) {
            return QueueName.CLINICAL_REVIEW;
        }
        if (NETWORK_TIER == NetworkTier.OUT_OF_NETWORK) {
            return QueueName.PAYER_FOLLOW_UP;
        }
        return QueueName.NONE;
    }

    public BigDecimal estimatedAllowedAmount(BigDecimal billedAmount, int units) {
        BigDecimal unitFactor = BigDecimal.valueOf(Math.max(1, units));
        BigDecimal networkFactor = NETWORK_TIER == NetworkTier.IN_NETWORK
                ? BigDecimal.valueOf(0.72)
                : BigDecimal.valueOf(0.41);
        return billedAmount.multiply(networkFactor).multiply(unitFactor);
    }

    public List<String> authorizationDisclosures() {
        return List.of(
                "benefitFamily=" + BENEFIT_FAMILY,
                "productLine=" + PRODUCT_LINE.name(),
                "networkTier=" + NETWORK_TIER.name(),
                "siteOfCare=" + SITE_OF_CARE.name());
    }

    public Map<String, String> auditAttributes(String referralId, LocalDate serviceDate, String modifier) {
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("referralId", referralId);
        attributes.put("serviceDate", serviceDate == null ? "" : serviceDate.toString());
        attributes.put("benefitFamily", BENEFIT_FAMILY);
        attributes.put("productLine", PRODUCT_LINE.name());
        attributes.put("networkTier", NETWORK_TIER.name());
        attributes.put("siteOfCare", SITE_OF_CARE.name());
        attributes.put("modifierExcluded", Boolean.toString(modifier != null && excludesModifier(modifier)));
        attributes.put("queueHint", defaultExceptionQueue(modifier).name());
        return Map.copyOf(attributes);
    }

    public boolean requiresManualBenefitReview(String modifier, BigDecimal billedAmount) {
        return (modifier != null && excludesModifier(modifier))
                || billedAmount.compareTo(BigDecimal.valueOf(5000)) > 0
                || NETWORK_TIER == NetworkTier.OUT_OF_NETWORK;
    }
}
