package com.careauth.compass.infrastructure.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policy_revision")
public class PolicyRevisionEntity {
    @Id
    private String id;
    private String tenantId;
    private String payerId;
    private String planCode;
    private String productLine;
    private String procedureCode;
    private String networkTier;
    private String siteOfCare;
    private LocalDate effectiveFrom;
    private LocalDate effectiveThrough;
    private LocalDateTime importedAt;
    private String status;
    private String outcome;
    private String queueName;

    protected PolicyRevisionEntity() {
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getPayerId() { return payerId; }
    public String getPlanCode() { return planCode; }
    public String getProductLine() { return productLine; }
    public String getProcedureCode() { return procedureCode; }
    public String getNetworkTier() { return networkTier; }
    public String getSiteOfCare() { return siteOfCare; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveThrough() { return effectiveThrough; }
    public LocalDateTime getImportedAt() { return importedAt; }
    public String getStatus() { return status; }
    public String getOutcome() { return outcome; }
    public String getQueueName() { return queueName; }
}
