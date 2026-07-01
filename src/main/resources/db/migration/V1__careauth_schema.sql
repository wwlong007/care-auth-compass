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
    status varchar(32) not null
);

create table referral_decision_snapshot (
    id varchar(64) primary key,
    referral_id varchar(64) not null,
    policy_revision_id varchar(64) not null,
    decision_outcome varchar(64) not null,
    queue_name varchar(64) not null,
    snapshot_locked boolean not null
);
