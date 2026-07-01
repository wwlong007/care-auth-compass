create table policy_revision (
    id varchar(64) primary key,
    tenant_id varchar(64) not null,
    payer_id varchar(64) not null,
    plan_code varchar(64) not null,
    product_line varchar(64) not null,
    procedure_code varchar(64) not null,
    network_tier varchar(64) not null,
    site_of_care varchar(64) not null,
    effective_from date not null,
    effective_through date not null,
    imported_at timestamp not null,
    status varchar(32) not null,
    outcome varchar(64) not null,
    queue_name varchar(64) not null
);

create table policy_requirement (
    id varchar(96) primary key,
    policy_revision_id varchar(64) not null,
    document_code varchar(64) not null,
    display_name varchar(255) not null,
    required boolean not null,
    sort_order integer not null,
    constraint fk_policy_requirement_revision
        foreign key (policy_revision_id) references policy_revision(id)
);

create table referral_decision_snapshot (
    id varchar(64) primary key,
    referral_id varchar(64) not null,
    policy_revision_id varchar(64) not null,
    decision_outcome varchar(64) not null,
    queue_name varchar(64) not null,
    snapshot_locked boolean not null
);

create index idx_policy_revision_scope on policy_revision (
    payer_id,
    plan_code,
    procedure_code,
    tenant_id,
    product_line,
    network_tier,
    site_of_care,
    effective_from,
    effective_through
);

create index idx_policy_requirement_revision on policy_requirement (policy_revision_id, sort_order);

insert into policy_revision (
    id, tenant_id, payer_id, plan_code, product_line, procedure_code, network_tier, site_of_care,
    effective_from, effective_through, imported_at, status, outcome, queue_name
) values
    ('EHP-MRI-2026Q1', 'evergreen-north', 'evergreen-health', 'EHP-PPO', 'COMMERCIAL',
     'MRI-LUMBAR', 'IN_NETWORK', 'HOSPITAL_OUTPATIENT', date '2026-01-01', date '2026-12-31',
     timestamp '2026-04-05 09:00:00', 'PUBLISHED', 'PENDING_DOCS', 'PAYER_FOLLOW_UP'),
    ('EHP-MRI-RETRO-202605', 'evergreen-north', 'evergreen-health', 'EHP-PPO', 'COMMERCIAL',
     'MRI-LUMBAR', 'IN_NETWORK', 'HOSPITAL_OUTPATIENT', date '2026-05-01', date '2026-12-31',
     timestamp '2026-06-10 14:00:00', 'PUBLISHED', 'CLINICAL_REVIEW', 'NURSE_REVIEW'),
    ('EHP-MRI-OON-RETRO-202605', 'evergreen-north', 'evergreen-health', 'EHP-PPO', 'COMMERCIAL',
     'MRI-LUMBAR', 'OUT_OF_NETWORK', 'HOSPITAL_OUTPATIENT', date '2026-05-01', date '2026-12-31',
     timestamp '2026-06-12 17:00:00', 'PUBLISHED', 'DENIED', 'CLINICAL_REVIEW'),
    ('EHP-MRI-MA-RETRO-202605', 'evergreen-north', 'evergreen-health', 'EHP-PPO', 'MEDICARE_ADVANTAGE',
     'MRI-LUMBAR', 'IN_NETWORK', 'HOSPITAL_OUTPATIENT', date '2026-05-01', date '2026-12-31',
     timestamp '2026-06-13 10:00:00', 'PUBLISHED', 'AUTH_REQUIRED', 'PAYER_FOLLOW_UP'),
    ('EHP-MRI-SOUTH-RETRO-202605', 'evergreen-south', 'evergreen-health', 'EHP-PPO', 'COMMERCIAL',
     'MRI-LUMBAR', 'IN_NETWORK', 'HOSPITAL_OUTPATIENT', date '2026-05-01', date '2026-12-31',
     timestamp '2026-06-14 10:00:00', 'PUBLISHED', 'NO_AUTH_REQUIRED', 'NONE'),
    ('EHP-MRI-IMG-RETRO-202605', 'evergreen-north', 'evergreen-health', 'EHP-PPO', 'COMMERCIAL',
     'MRI-LUMBAR', 'IN_NETWORK', 'IMAGING_CENTER', date '2026-05-01', date '2026-12-31',
     timestamp '2026-06-16 08:30:00', 'PUBLISHED', 'AUTH_REQUIRED', 'PAYER_FOLLOW_UP'),
    ('AHP-MRI-2026Q1', 'aurora-west', 'aurora-health', 'AHP-EPO', 'EXCHANGE',
     'MRI-LUMBAR', 'TIER_TWO', 'IMAGING_CENTER', date '2026-01-01', date '2026-12-31',
     timestamp '2026-04-01 11:00:00', 'PUBLISHED', 'PENDING_DOCS', 'PAYER_FOLLOW_UP'),
    ('AHP-MRI-RETRO-202606', 'aurora-west', 'aurora-health', 'AHP-EPO', 'EXCHANGE',
     'MRI-LUMBAR', 'TIER_TWO', 'IMAGING_CENTER', date '2026-06-01', date '2026-12-31',
     timestamp '2026-06-18 16:45:00', 'PUBLISHED', 'AUTH_REQUIRED', 'CLINICAL_REVIEW');

insert into policy_requirement (
    id, policy_revision_id, document_code, display_name, required, sort_order
) values
    ('req-ehp-q1-pt', 'EHP-MRI-2026Q1', 'PT-NOTES', 'Six weeks conservative therapy', true, 10),
    ('req-ehp-q1-xray', 'EHP-MRI-2026Q1', 'XRAY-REPORT', 'Recent x-ray report', true, 20),
    ('req-ehp-retro-specialist', 'EHP-MRI-RETRO-202605', 'SPECIALIST-ORDER',
     'Specialist order confirming medical necessity', true, 10),
    ('req-ehp-oon-exception', 'EHP-MRI-OON-RETRO-202605', 'OON-EXCEPTION',
     'Out-of-network exception packet', true, 10),
    ('req-ehp-ma-coverage', 'EHP-MRI-MA-RETRO-202605', 'MA-CMS-COVERAGE',
     'Medicare coverage memo', true, 10),
    ('req-ehp-img-accreditation', 'EHP-MRI-IMG-RETRO-202605', 'FACILITY-ACCREDITATION',
     'Imaging center accreditation attestation', true, 10),
    ('req-ahp-q1-conservative', 'AHP-MRI-2026Q1', 'CONSERVATIVE-CARE',
     'Conservative-care attestation', true, 10),
    ('req-ahp-retro-prior-study', 'AHP-MRI-RETRO-202606', 'IMAGING-PRIOR-STUDY',
     'Prior imaging study report', true, 10);
