package com.careauth.compass.application.scenario;

import com.careauth.compass.application.authorization.AuthorizationDecisionService;
import com.careauth.compass.application.authorization.ChecklistMaterializer;
import com.careauth.compass.application.authorization.CoveragePolicyResolver;
import com.careauth.compass.application.jobs.NightlyQueueRefreshJob;
import com.careauth.compass.application.jobs.LegacyQueueDecisionAssembler;
import com.careauth.compass.application.referral.ReferralReevaluationService;
import com.careauth.compass.application.workqueue.WorkQueueService;
import com.careauth.compass.domain.repository.AuditRepository;
import com.careauth.compass.domain.repository.OutboxRepository;
import com.careauth.compass.domain.repository.PolicyRevisionRepository;
import com.careauth.compass.domain.repository.ReferralRepository;
import com.careauth.compass.infrastructure.cache.PolicyRuleCache;

public record CareAuthRuntime(
        PolicyRevisionRepository policyRepository,
        ReferralRepository referralRepository,
        AuditRepository auditRepository,
        OutboxRepository outboxRepository,
        AuthorizationDecisionService decisionService,
        ReferralReevaluationService reevaluationService,
        NightlyQueueRefreshJob nightlyQueueRefreshJob,
        WorkQueueService workQueueService
) {
    public static CareAuthRuntime assemble(PolicyRevisionRepository policyRepository,
                                           ReferralRepository referralRepository,
                                           AuditRepository auditRepository,
                                           OutboxRepository outboxRepository) {
        PolicyRuleCache policyRuleCache = new PolicyRuleCache(policyRepository);
        CoveragePolicyResolver resolver = new CoveragePolicyResolver(policyRuleCache);
        AuthorizationDecisionService decisionService = new AuthorizationDecisionService(resolver);
        ChecklistMaterializer checklistMaterializer = new ChecklistMaterializer();
        ReferralReevaluationService reevaluationService = new ReferralReevaluationService(
                referralRepository, policyRepository, decisionService, checklistMaterializer,
                auditRepository, outboxRepository);
        LegacyQueueDecisionAssembler legacyQueueDecisionAssembler = new LegacyQueueDecisionAssembler(policyRepository);
        NightlyQueueRefreshJob job = new NightlyQueueRefreshJob(referralRepository, legacyQueueDecisionAssembler);
        WorkQueueService workQueueService = new WorkQueueService(referralRepository);
        return new CareAuthRuntime(policyRepository, referralRepository, auditRepository,
                outboxRepository, decisionService, reevaluationService, job, workQueueService);
    }
}
