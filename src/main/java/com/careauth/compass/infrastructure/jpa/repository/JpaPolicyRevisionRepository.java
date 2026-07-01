package com.careauth.compass.infrastructure.jpa.repository;

import com.careauth.compass.infrastructure.jpa.entity.PolicyRevisionEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPolicyRevisionRepository extends JpaRepository<PolicyRevisionEntity, String> {
    List<PolicyRevisionEntity> findByPayerIdAndPlanCodeAndProcedureCode(String payerId, String planCode, String procedureCode);

    @Query("select p from PolicyRevisionEntity p "
            + "where p.tenantId = :tenantId "
            + "and p.payerId = :payerId "
            + "and p.planCode = :planCode "
            + "and p.productLine = :productLine "
            + "and p.procedureCode = :procedureCode "
            + "and p.networkTier = :networkTier "
            + "and p.siteOfCare = :siteOfCare "
            + "and p.status = 'PUBLISHED' "
            + "and p.effectiveFrom <= :serviceDate "
            + "and p.effectiveThrough >= :serviceDate "
            + "order by p.effectiveFrom desc, p.importedAt desc")
    List<PolicyRevisionEntity> findEffectiveRevision(
            @Param("tenantId") String tenantId,
            @Param("payerId") String payerId,
            @Param("planCode") String planCode,
            @Param("productLine") String productLine,
            @Param("procedureCode") String procedureCode,
            @Param("networkTier") String networkTier,
            @Param("siteOfCare") String siteOfCare,
            @Param("serviceDate") LocalDate serviceDate);
}
