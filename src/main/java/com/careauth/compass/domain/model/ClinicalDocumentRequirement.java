package com.careauth.compass.domain.model;

import java.util.Objects;

public record ClinicalDocumentRequirement(
        String documentCode,
        String displayName,
        boolean required,
        DocumentStatus status,
        String policyRevisionId
) {
    public ClinicalDocumentRequirement {
        Objects.requireNonNull(documentCode, "documentCode");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(policyRevisionId, "policyRevisionId");
    }

    public ClinicalDocumentRequirement withPolicyRevisionId(String revisionId) {
        return new ClinicalDocumentRequirement(documentCode, displayName, required, status, revisionId);
    }
}
