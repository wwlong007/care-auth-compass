package com.careauth.compass.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CoveragePolicyRevision {
    private final String id;
    private final String tenantId;
    private final String payerId;
    private final String planCode;
    private final ProductLine productLine;
    private final String procedureCode;
    private final NetworkTier networkTier;
    private final SiteOfCare siteOfCare;
    private final LocalDate effectiveFrom;
    private final LocalDate effectiveThrough;
    private final LocalDateTime importedAt;
    private final PolicyStatus status;
    private final DecisionOutcome outcome;
    private final QueueName queueName;
    private final List<ClinicalDocumentRequirement> requirements;

    public CoveragePolicyRevision(String id, String tenantId, String payerId, String planCode,
                                  ProductLine productLine, String procedureCode, NetworkTier networkTier,
                                  SiteOfCare siteOfCare, LocalDate effectiveFrom, LocalDate effectiveThrough,
                                  LocalDateTime importedAt, PolicyStatus status, DecisionOutcome outcome,
                                  QueueName queueName, List<ClinicalDocumentRequirement> requirements) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.payerId = Objects.requireNonNull(payerId, "payerId");
        this.planCode = Objects.requireNonNull(planCode, "planCode");
        this.productLine = Objects.requireNonNull(productLine, "productLine");
        this.procedureCode = Objects.requireNonNull(procedureCode, "procedureCode");
        this.networkTier = Objects.requireNonNull(networkTier, "networkTier");
        this.siteOfCare = Objects.requireNonNull(siteOfCare, "siteOfCare");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        this.effectiveThrough = Objects.requireNonNull(effectiveThrough, "effectiveThrough");
        this.importedAt = Objects.requireNonNull(importedAt, "importedAt");
        this.status = Objects.requireNonNull(status, "status");
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.queueName = Objects.requireNonNull(queueName, "queueName");
        this.requirements = List.copyOf(requirements);
    }

    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String payerId() { return payerId; }
    public String planCode() { return planCode; }
    public ProductLine productLine() { return productLine; }
    public String procedureCode() { return procedureCode; }
    public NetworkTier networkTier() { return networkTier; }
    public SiteOfCare siteOfCare() { return siteOfCare; }
    public LocalDate effectiveFrom() { return effectiveFrom; }
    public LocalDate effectiveThrough() { return effectiveThrough; }
    public LocalDateTime importedAt() { return importedAt; }
    public PolicyStatus status() { return status; }
    public DecisionOutcome outcome() { return outcome; }
    public QueueName queueName() { return queueName; }
    public List<ClinicalDocumentRequirement> requirements() { return requirements; }

    public boolean isPublished() {
        return status == PolicyStatus.PUBLISHED;
    }

    public boolean isEffectiveOn(LocalDate serviceDate) {
        return !serviceDate.isBefore(effectiveFrom) && !serviceDate.isAfter(effectiveThrough);
    }

    public boolean matchesExactly(AuthorizationRequest request) {
        return tenantId.equals(request.tenantId())
                && payerId.equals(request.payerId())
                && planCode.equals(request.planCode())
                && productLine == request.productLine()
                && procedureCode.equals(request.procedureCode())
                && networkTier == request.networkTier()
                && siteOfCare == request.siteOfCare();
    }

    public boolean belongsToSamePayerProcedure(AuthorizationRequest request) {
        return payerId.equals(request.payerId())
                && planCode.equals(request.planCode())
                && procedureCode.equals(request.procedureCode());
    }

    public int specificityScore() {
        int score = 0;
        if (!tenantId.isBlank()) score += 16;
        if (!payerId.isBlank()) score += 8;
        if (!planCode.isBlank()) score += 4;
        if (!procedureCode.isBlank()) score += 2;
        if (networkTier != null && siteOfCare != null) score += 1;
        return score;
    }
}
