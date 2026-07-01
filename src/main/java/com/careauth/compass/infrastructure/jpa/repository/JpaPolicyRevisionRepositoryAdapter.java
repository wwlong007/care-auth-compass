package com.careauth.compass.infrastructure.jpa.repository;

import com.careauth.compass.domain.model.AuthorizationRequest;
import com.careauth.compass.domain.model.CoveragePolicyRevision;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.careauth.compass.infrastructure.jpa.PolicyRevisionEntityMapper;
import com.careauth.compass.infrastructure.jpa.entity.PolicyRequirementEntity;
import com.careauth.compass.infrastructure.jpa.entity.PolicyRevisionEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JpaPolicyRevisionRepositoryAdapter implements PolicyRevisionRepository {
    private final JpaPolicyRevisionRepository policyRevisionRepository;
    private final JpaPolicyRequirementRepository policyRequirementRepository;
    private final PolicyRevisionEntityMapper mapper;

    public JpaPolicyRevisionRepositoryAdapter(JpaPolicyRevisionRepository policyRevisionRepository,
                                              JpaPolicyRequirementRepository policyRequirementRepository,
                                              PolicyRevisionEntityMapper mapper) {
        this.policyRevisionRepository = policyRevisionRepository;
        this.policyRequirementRepository = policyRequirementRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CoveragePolicyRevision> findAll() {
        List<PolicyRevisionEntity> revisions = policyRevisionRepository.findAll();
        return mapWithRequirements(revisions);
    }

    @Override
    public List<CoveragePolicyRevision> findByPayerPlanProcedure(String payerId, String planCode, String procedureCode) {
        return mapWithRequirements(policyRevisionRepository.findByPayerIdAndPlanCodeAndProcedureCode(
                payerId, planCode, procedureCode));
    }

    @Override
    public Optional<CoveragePolicyRevision> findEffectiveFor(AuthorizationRequest request) {
        return policyRevisionRepository.findEffectiveRevision(
                        request.tenantId(),
                        request.payerId(),
                        request.planCode(),
                        request.productLine().name(),
                        request.procedureCode(),
                        request.networkTier().name(),
                        request.siteOfCare().name(),
                        request.serviceDate())
                .stream()
                .findFirst()
                .map(entity -> mapper.toDomain(entity,
                        policyRequirementRepository.findByPolicyRevisionIdOrderBySortOrderAsc(entity.getId())));
    }

    private List<CoveragePolicyRevision> mapWithRequirements(List<PolicyRevisionEntity> revisions) {
        List<String> revisionIds = revisions.stream()
                .map(PolicyRevisionEntity::getId)
                .toList();
        Map<String, List<PolicyRequirementEntity>> requirementsByRevision = policyRequirementRepository
                .findByPolicyRevisionIdIn(revisionIds)
                .stream()
                .collect(Collectors.groupingBy(PolicyRequirementEntity::getPolicyRevisionId));
        return revisions.stream()
                .map(entity -> mapper.toDomain(entity,
                        requirementsByRevision.getOrDefault(entity.getId(), List.of())))
                .toList();
    }
}
