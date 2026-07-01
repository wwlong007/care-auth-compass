package com.careauth.compass.application.config;

import com.careauth.compass.application.authorization.AuthorizationDecisionService;
import com.careauth.compass.application.authorization.ChecklistMaterializer;
import com.careauth.compass.application.authorization.CoveragePolicyResolver;
import com.careauth.compass.application.jobs.LegacyQueueDecisionAssembler;
import com.careauth.compass.application.jobs.NightlyQueueRefreshJob;
import com.careauth.compass.application.referral.ReferralReevaluationService;
import com.careauth.compass.application.scenario.ScenarioDataFactory;
import com.careauth.compass.application.workqueue.WorkQueueService;
import com.careauth.compass.domain.repository.AuditRepository;
import com.careauth.compass.domain.repository.OutboxRepository;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.careauth.compass.domain.repository.ReferralRepository;
import com.careauth.compass.infrastructure.cache.PolicyRuleCache;
import com.careauth.compass.infrastructure.jpa.PolicyRevisionEntityMapper;
import com.careauth.compass.infrastructure.jpa.repository.JpaPolicyRequirementRepository;
import com.careauth.compass.infrastructure.jpa.repository.JpaPolicyRevisionRepository;
import com.careauth.compass.infrastructure.jpa.repository.JpaPolicyRevisionRepositoryAdapter;
import com.careauth.compass.infrastructure.repository.InMemoryAuditRepository;
import com.careauth.compass.infrastructure.repository.InMemoryOutboxRepository;
import com.careauth.compass.infrastructure.repository.InMemoryReferralRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CareAuthServiceConfiguration {
    @Bean
    public PolicyRevisionEntityMapper policyRevisionEntityMapper() {
        return new PolicyRevisionEntityMapper();
    }

    @Bean
    public PolicyRevisionRepository policyRevisionRepository(
            JpaPolicyRevisionRepository policyRevisionRepository,
            JpaPolicyRequirementRepository policyRequirementRepository,
            PolicyRevisionEntityMapper mapper) {
        return new JpaPolicyRevisionRepositoryAdapter(policyRevisionRepository, policyRequirementRepository, mapper);
    }

    @Bean
    public ReferralRepository referralRepository() {
        return new InMemoryReferralRepository(ScenarioDataFactory.referrals());
    }

    @Bean
    public AuditRepository auditRepository() {
        return new InMemoryAuditRepository();
    }

    @Bean
    public OutboxRepository outboxRepository() {
        return new InMemoryOutboxRepository();
    }

    @Bean
    public PolicyRuleCache policyRuleCache(PolicyRevisionRepository policyRevisionRepository) {
        return new PolicyRuleCache(policyRevisionRepository);
    }

    @Bean
    public CoveragePolicyResolver coveragePolicyResolver(PolicyRuleCache policyRuleCache) {
        return new CoveragePolicyResolver(policyRuleCache);
    }

    @Bean
    public AuthorizationDecisionService authorizationDecisionService(CoveragePolicyResolver resolver) {
        return new AuthorizationDecisionService(resolver);
    }

    @Bean
    public ChecklistMaterializer checklistMaterializer() {
        return new ChecklistMaterializer();
    }

    @Bean
    public ReferralReevaluationService referralReevaluationService(
            ReferralRepository referralRepository,
            PolicyRevisionRepository policyRevisionRepository,
            AuthorizationDecisionService decisionService,
            ChecklistMaterializer checklistMaterializer,
            AuditRepository auditRepository,
            OutboxRepository outboxRepository) {
        return new ReferralReevaluationService(referralRepository, policyRevisionRepository, decisionService,
                checklistMaterializer, auditRepository, outboxRepository);
    }

    @Bean
    public LegacyQueueDecisionAssembler legacyQueueDecisionAssembler(PolicyRevisionRepository policyRevisionRepository) {
        return new LegacyQueueDecisionAssembler(policyRevisionRepository);
    }

    @Bean
    public NightlyQueueRefreshJob nightlyQueueRefreshJob(
            ReferralRepository referralRepository,
            LegacyQueueDecisionAssembler legacyQueueDecisionAssembler) {
        return new NightlyQueueRefreshJob(referralRepository, legacyQueueDecisionAssembler);
    }

    @Bean
    public WorkQueueService workQueueService(ReferralRepository referralRepository) {
        return new WorkQueueService(referralRepository);
    }
}
