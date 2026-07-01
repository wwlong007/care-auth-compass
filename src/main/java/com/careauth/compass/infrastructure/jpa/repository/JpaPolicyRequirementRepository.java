package com.careauth.compass.infrastructure.jpa.repository;

import com.careauth.compass.infrastructure.jpa.entity.PolicyRequirementEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPolicyRequirementRepository extends JpaRepository<PolicyRequirementEntity, String> {
    List<PolicyRequirementEntity> findByPolicyRevisionIdIn(Collection<String> policyRevisionIds);

    List<PolicyRequirementEntity> findByPolicyRevisionIdOrderBySortOrderAsc(String policyRevisionId);
}
