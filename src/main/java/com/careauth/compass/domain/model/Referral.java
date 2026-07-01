package com.careauth.compass.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Referral {
    private final String id;
    private final String tenantId;
    private final String payerId;
    private final String planCode;
    private final ProductLine productLine;
    private final String memberId;
    private final String procedureCode;
    private final NetworkTier networkTier;
    private final SiteOfCare siteOfCare;
    private final LocalDate serviceDate;
    private ReferralStatus status;
    private ReferralDecisionSnapshot decisionSnapshot;
    private List<ClinicalDocumentRequirement> requiredDocuments;

    public Referral(String id, String tenantId, String payerId, String planCode, ProductLine productLine,
                    String memberId, String procedureCode, NetworkTier networkTier, SiteOfCare siteOfCare,
                    LocalDate serviceDate, ReferralStatus status, ReferralDecisionSnapshot decisionSnapshot,
                    List<ClinicalDocumentRequirement> requiredDocuments) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.payerId = Objects.requireNonNull(payerId, "payerId");
        this.planCode = Objects.requireNonNull(planCode, "planCode");
        this.productLine = Objects.requireNonNull(productLine, "productLine");
        this.memberId = Objects.requireNonNull(memberId, "memberId");
        this.procedureCode = Objects.requireNonNull(procedureCode, "procedureCode");
        this.networkTier = Objects.requireNonNull(networkTier, "networkTier");
        this.siteOfCare = Objects.requireNonNull(siteOfCare, "siteOfCare");
        this.serviceDate = Objects.requireNonNull(serviceDate, "serviceDate");
        this.status = Objects.requireNonNull(status, "status");
        this.decisionSnapshot = Objects.requireNonNull(decisionSnapshot, "decisionSnapshot");
        this.requiredDocuments = new ArrayList<>(requiredDocuments);
    }

    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String payerId() { return payerId; }
    public String planCode() { return planCode; }
    public ProductLine productLine() { return productLine; }
    public String memberId() { return memberId; }
    public String procedureCode() { return procedureCode; }
    public NetworkTier networkTier() { return networkTier; }
    public SiteOfCare siteOfCare() { return siteOfCare; }
    public LocalDate serviceDate() { return serviceDate; }
    public ReferralStatus status() { return status; }
    public ReferralDecisionSnapshot decisionSnapshot() { return decisionSnapshot; }
    public List<ClinicalDocumentRequirement> requiredDocuments() { return List.copyOf(requiredDocuments); }

    public AuthorizationRequest toAuthorizationRequest() {
        return new AuthorizationRequest(tenantId, payerId, planCode, productLine, memberId, id,
                procedureCode, networkTier, siteOfCare, serviceDate);
    }

    public boolean isSnapshotLocked() {
        return decisionSnapshot.snapshotLocked() || status.isLockedForRetroReevaluation();
    }

    public void replaceRequiredDocuments(List<ClinicalDocumentRequirement> documents) {
        this.requiredDocuments = new ArrayList<>(documents);
    }

    public void applyDecision(AuthorizationDecision decision, boolean lockSnapshot) {
        this.decisionSnapshot = new ReferralDecisionSnapshot(
                UUID.nameUUIDFromBytes((id + ":" + decision.policyRevisionId()).getBytes()).toString(),
                id,
                decision.policyRevisionId(),
                decision.outcome(),
                decision.queueName(),
                lockSnapshot,
                Instant.parse("2026-06-15T08:30:00Z"),
                decision.requirements());
        this.requiredDocuments = new ArrayList<>(decision.requirements());
        if (status.isMutableForRetroReevaluation()) {
            if (decision.outcome() == DecisionOutcome.PENDING_DOCS) {
                this.status = ReferralStatus.PENDING_DOCS;
            } else if (decision.queueName() == QueueName.CLINICAL_REVIEW || decision.queueName() == QueueName.NURSE_REVIEW) {
                this.status = ReferralStatus.CLINICAL_REVIEW;
            }
        }
    }
}
