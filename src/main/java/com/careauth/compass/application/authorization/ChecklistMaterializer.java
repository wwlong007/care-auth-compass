package com.careauth.compass.application.authorization;

import com.careauth.compass.domain.model.ClinicalDocumentRequirement;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.policy.ClinicalChecklistKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChecklistMaterializer {
    private final Map<ClinicalChecklistKey, List<ClinicalDocumentRequirement>> cache = new ConcurrentHashMap<>();

    public List<ClinicalDocumentRequirement> materialize(Referral referral, CoveragePolicyRevision revision) {
        ClinicalChecklistKey key = ClinicalChecklistKey.of(referral, revision.id());
        List<ClinicalDocumentRequirement> existing = cache.getOrDefault(key, referral.requiredDocuments());
        Map<String, ClinicalDocumentRequirement> merged = new LinkedHashMap<>();
        for (ClinicalDocumentRequirement requirement : existing) {
            merged.putIfAbsent(requirement.documentCode(), requirement);
        }
        for (ClinicalDocumentRequirement requirement : revision.requirements()) {
            merged.putIfAbsent(requirement.documentCode(), requirement.withPolicyRevisionId(revision.id()));
        }
        List<ClinicalDocumentRequirement> materialized = new ArrayList<>(merged.values());
        cache.put(key, materialized);
        referral.replaceRequiredDocuments(materialized);
        return List.copyOf(materialized);
    }
}
