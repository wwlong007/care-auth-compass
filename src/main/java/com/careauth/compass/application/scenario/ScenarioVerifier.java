package com.careauth.compass.application.scenario;

import com.careauth.compass.domain.model.AuthorizationDecision;
import com.careauth.compass.domain.model.QueueName;
import com.careauth.compass.domain.model.Referral;
import java.util.ArrayList;
import java.util.List;

public class ScenarioVerifier {
    public int verify(String scenarioName, boolean print) {
        if (!"evergreen-retro-amendment".equals(scenarioName)) {
            if (print) {
                System.err.println("Unknown scenario: " + scenarioName);
            }
            return 2;
        }
        CareAuthRuntime runtime = ScenarioDataFactory.evergreenRetroAmendmentRuntime();
        List<String> errors = verifyEvergreen(runtime);
        if (print) {
            if (errors.isEmpty()) {
                System.out.println("evergreen-retro-amendment: PASS");
            } else {
                System.out.println("evergreen-retro-amendment: FAIL");
                errors.forEach(error -> System.out.println(" - " + error));
            }
        }
        return errors.isEmpty() ? 0 : 1;
    }

    public List<String> verifyEvergreen(CareAuthRuntime runtime) {
        List<String> errors = new ArrayList<>();
        AuthorizationDecision restDecision = runtime.decisionService()
                .evaluate(runtime.referralRepository().require("ref-evergreen-retro-mutable").toAuthorizationRequest());
        runtime.nightlyQueueRefreshJob().refreshOpenQueues();
        Referral mutable = runtime.referralRepository().require("ref-evergreen-retro-mutable");
        Referral locked = runtime.referralRepository().require("ref-evergreen-retro-locked");
        if (!"EHP-MRI-RETRO-202605".equals(restDecision.policyRevisionId())) {
            errors.add("REST decision did not select the Evergreen retro policy revision");
        }
        if (!"EHP-MRI-RETRO-202605".equals(mutable.decisionSnapshot().policyRevisionId())) {
            errors.add("Mutable referral was not refreshed to the retro policy revision");
        }
        if (mutable.requiredDocuments().stream().noneMatch(doc -> doc.documentCode().equals("SPECIALIST-ORDER"))) {
            errors.add("Mutable referral is missing the retro specialist-order checklist item");
        }
        if (mutable.requiredDocuments().stream().anyMatch(doc -> doc.documentCode().equals("XRAY-REPORT"))) {
            errors.add("Mutable referral retained a stale x-ray checklist item");
        }
        if (!"EHP-MRI-2026Q1".equals(locked.decisionSnapshot().policyRevisionId())) {
            errors.add("Locked scheduled referral snapshot was overwritten by retro refresh");
        }
        if (runtime.auditRepository().findAll().stream().noneMatch(audit ->
                audit.referralId().equals("ref-evergreen-retro-locked"))) {
            errors.add("Locked scheduled referral did not receive an audit observation");
        }
        if (runtime.workQueueService().findItems(QueueName.NURSE_REVIEW).stream()
                .noneMatch(item -> item.referralId().equals("ref-evergreen-retro-mutable"))) {
            errors.add("Mutable referral is absent from the nurse review queue");
        }
        return errors;
    }
}
