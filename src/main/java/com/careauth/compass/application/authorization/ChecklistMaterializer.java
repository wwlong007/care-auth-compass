package com.careauth.compass.application.authorization;

import com.careauth.compass.domain.model.ClinicalDocumentRequirement;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.policy.ClinicalChecklistKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChecklistMaterializer {
    private final ChecklistReplacementStrategy replacementStrategy = new ChecklistReplacementStrategy();
    private final Map<ClinicalChecklistKey, List<ClinicalDocumentRequirement>> cache = new ConcurrentHashMap<>();

    public List<ClinicalDocumentRequirement> materialize(Referral referral, CoveragePolicyRevision revision) {
        ClinicalChecklistKey key = ClinicalChecklistKey.of(referral, revision.id());
        List<ClinicalDocumentRequirement> materialized = cache.computeIfAbsent(key, ignored ->
                replacementStrategy.materialize(referral, revision));
        cache.put(key, materialized);
        referral.replaceRequiredDocuments(materialized);
        return List.copyOf(materialized);
    }
}
