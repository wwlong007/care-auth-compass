package com.careauth.compass.infrastructure.jpa;

import com.careauth.compass.domain.model.ClinicalDocumentRequirement;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.model.DecisionOutcome;
import com.careauth.compass.domain.model.DocumentStatus;
import com.careauth.compass.domain.model.NetworkTier;
import com.careauth.compass.domain.model.PolicyStatus;
import com.careauth.compass.domain.model.ProductLine;
import com.careauth.compass.domain.model.QueueName;
import com.careauth.compass.domain.model.SiteOfCare;
import com.careauth.compass.infrastructure.jpa.entity.PolicyRequirementEntity;
import com.careauth.compass.infrastructure.jpa.entity.PolicyRevisionEntity;
import java.util.Comparator;
import java.util.List;

public class PolicyRevisionEntityMapper {
    public CoveragePolicyRevision toDomain(
            PolicyRevisionEntity entity,
            List<PolicyRequirementEntity> requirementEntities) {
        List<ClinicalDocumentRequirement> requirements = requirementEntities.stream()
                .filter(item -> item.getPolicyRevisionId().equals(entity.getId()))
                .sorted(Comparator.comparingInt(PolicyRequirementEntity::getSortOrder))
                .map(item -> new ClinicalDocumentRequirement(
                        item.getDocumentCode(),
                        item.getDisplayName(),
                        item.isRequired(),
                        DocumentStatus.MISSING,
                        entity.getId()))
                .toList();
        return new CoveragePolicyRevision(
                entity.getId(),
                entity.getTenantId(),
                entity.getPayerId(),
                entity.getPlanCode(),
                ProductLine.valueOf(entity.getProductLine()),
                entity.getProcedureCode(),
                NetworkTier.valueOf(entity.getNetworkTier()),
                SiteOfCare.valueOf(entity.getSiteOfCare()),
                entity.getEffectiveFrom(),
                entity.getEffectiveThrough(),
                entity.getImportedAt(),
                PolicyStatus.valueOf(entity.getStatus()),
                DecisionOutcome.valueOf(entity.getOutcome()),
                QueueName.valueOf(entity.getQueueName()),
                requirements);
    }
}
