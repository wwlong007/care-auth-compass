package com.careauth.compass.application.scenario;

import com.careauth.compass.domain.model.*;
import com.careauth.compass.infrastructure.repository.InMemoryAuditRepository;
import com.careauth.compass.infrastructure.repository.InMemoryOutboxRepository;
import com.careauth.compass.infrastructure.repository.InMemoryPolicyRevisionRepository;
import com.careauth.compass.infrastructure.repository.InMemoryReferralRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ScenarioDataFactory {
    private ScenarioDataFactory() {
    }

    public static CareAuthRuntime evergreenRetroAmendmentRuntime() {
        return CareAuthRuntime.assemble(
                new InMemoryPolicyRevisionRepository(policyRevisions()),
                new InMemoryReferralRepository(referrals()),
                new InMemoryAuditRepository(),
                new InMemoryOutboxRepository());
    }

    public static List<CoveragePolicyRevision> policyRevisions() {
        return List.of(
                revision("EHP-MRI-2026Q1", "evergreen-north", ProductLine.COMMERCIAL,
                        NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 4, 5, 9, 0), DecisionOutcome.PENDING_DOCS,
                        QueueName.PAYER_FOLLOW_UP,
                        List.of(doc("PT-NOTES", "Six weeks conservative therapy", "EHP-MRI-2026Q1"),
                                doc("XRAY-REPORT", "Recent x-ray report", "EHP-MRI-2026Q1"))),
                revision("EHP-MRI-RETRO-202605", "evergreen-north", ProductLine.COMMERCIAL,
                        NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 6, 10, 14, 0), DecisionOutcome.CLINICAL_REVIEW,
                        QueueName.NURSE_REVIEW,
                        List.of(doc("SPECIALIST-ORDER", "Specialist order confirming medical necessity", "EHP-MRI-RETRO-202605"))),
                revision("EHP-MRI-OON-RETRO-202605", "evergreen-north", ProductLine.COMMERCIAL,
                        NetworkTier.OUT_OF_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 6, 12, 17, 0), DecisionOutcome.DENIED,
                        QueueName.CLINICAL_REVIEW,
                        List.of(doc("OON-EXCEPTION", "Out-of-network exception packet", "EHP-MRI-OON-RETRO-202605"))),
                revision("EHP-MRI-MA-RETRO-202605", "evergreen-north", ProductLine.MEDICARE_ADVANTAGE,
                        NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 6, 13, 10, 0), DecisionOutcome.AUTH_REQUIRED,
                        QueueName.PAYER_FOLLOW_UP,
                        List.of(doc("MA-CMS-COVERAGE", "Medicare coverage memo", "EHP-MRI-MA-RETRO-202605"))),
                revision("EHP-MRI-SOUTH-RETRO-202605", "evergreen-south", ProductLine.COMMERCIAL,
                        NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 1), LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 6, 14, 10, 0), DecisionOutcome.NO_AUTH_REQUIRED,
                        QueueName.NONE,
                        List.of())
        );
    }

    public static List<Referral> referrals() {
        return List.of(
                referral("ref-evergreen-retro-mutable", ReferralStatus.PENDING_AUTH, false,
                        ProductLine.COMMERCIAL, NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 20)),
                referral("ref-evergreen-retro-locked", ReferralStatus.SCHEDULED, true,
                        ProductLine.COMMERCIAL, NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 20)),
                referral("ref-evergreen-before-retro", ReferralStatus.PENDING_AUTH, false,
                        ProductLine.COMMERCIAL, NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 4, 20)),
                referral("ref-evergreen-oon", ReferralStatus.PENDING_AUTH, false,
                        ProductLine.COMMERCIAL, NetworkTier.OUT_OF_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 20)),
                referral("ref-evergreen-ma", ReferralStatus.PENDING_AUTH, false,
                        ProductLine.MEDICARE_ADVANTAGE, NetworkTier.IN_NETWORK, SiteOfCare.HOSPITAL_OUTPATIENT,
                        LocalDate.of(2026, 5, 20))
        );
    }

    private static CoveragePolicyRevision revision(String id, String tenantId, ProductLine productLine,
                                                   NetworkTier networkTier, SiteOfCare siteOfCare,
                                                   LocalDate from, LocalDate through, LocalDateTime importedAt,
                                                   DecisionOutcome outcome, QueueName queueName,
                                                   List<ClinicalDocumentRequirement> requirements) {
        return new CoveragePolicyRevision(id, tenantId, "evergreen-health", "EHP-PPO", productLine,
                "MRI-LUMBAR", networkTier, siteOfCare, from, through, importedAt,
                PolicyStatus.PUBLISHED, outcome, queueName, requirements);
    }

    private static ClinicalDocumentRequirement doc(String code, String name, String revisionId) {
        return new ClinicalDocumentRequirement(code, name, true, DocumentStatus.MISSING, revisionId);
    }

    private static Referral referral(String id, ReferralStatus status, boolean locked, ProductLine productLine,
                                     NetworkTier networkTier, SiteOfCare siteOfCare, LocalDate serviceDate) {
        List<ClinicalDocumentRequirement> stale = List.of(
                doc("PT-NOTES", "Six weeks conservative therapy", "EHP-MRI-2026Q1"),
                doc("XRAY-REPORT", "Recent x-ray report", "EHP-MRI-2026Q1"));
        ReferralDecisionSnapshot snapshot = new ReferralDecisionSnapshot(
                "snap-" + id,
                id,
                "EHP-MRI-2026Q1",
                DecisionOutcome.PENDING_DOCS,
                QueueName.PAYER_FOLLOW_UP,
                locked,
                Instant.parse("2026-05-05T10:00:00Z"),
                stale);
        return new Referral(id, "evergreen-north", "evergreen-health", "EHP-PPO", productLine,
                "member-" + id, "MRI-LUMBAR", networkTier, siteOfCare, serviceDate, status, snapshot, stale);
    }
}
