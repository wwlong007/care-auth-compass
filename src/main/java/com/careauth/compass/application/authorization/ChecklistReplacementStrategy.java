package com.careauth.compass.application.authorization;

import com.careauth.compass.domain.model.ClinicalDocumentRequirement;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChecklistReplacementStrategy {
    public List<ClinicalDocumentRequirement> materialize(Referral referral, CoveragePolicyRevision revision) {
        Map<String, ClinicalDocumentRequirement> documents = new LinkedHashMap<>();
        for (ClinicalDocumentRequirement existing : referral.requiredDocuments()) {
            documents.putIfAbsent(existing.documentCode(), existing);
        }
        for (ClinicalDocumentRequirement requirement : revision.requirements()) {
            documents.putIfAbsent(requirement.documentCode(), requirement.withPolicyRevisionId(revision.id()));
        }
        return new ArrayList<>(documents.values());
    }
}
