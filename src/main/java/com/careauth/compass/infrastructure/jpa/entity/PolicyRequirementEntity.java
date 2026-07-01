package com.careauth.compass.infrastructure.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policy_requirement")
public class PolicyRequirementEntity {
    @Id
    private String id;
    private String policyRevisionId;
    private String documentCode;
    private String displayName;
    private boolean required;
    private int sortOrder;

    protected PolicyRequirementEntity() {
    }

    public String getId() { return id; }
    public String getPolicyRevisionId() { return policyRevisionId; }
    public String getDocumentCode() { return documentCode; }
    public String getDisplayName() { return displayName; }
    public boolean isRequired() { return required; }
    public int getSortOrder() { return sortOrder; }
}
