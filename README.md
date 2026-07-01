# CareAuth Compass

CareAuth Compass is an enterprise prior authorization platform for referral
intake, payer policy evaluation, clinical checklist generation, work queue
routing, audit observation, and outbox publishing.

The service includes domain policies, Spring API boundaries, JPA-backed policy
storage, in-memory adapters for local scenario verification, payer integration
adapters, reporting projections, and a CLI verifier for payer policy incidents.

## Build

Use Java 21 or newer.

```bash
mvn -q -DskipTests package
java -jar target/careauth-compass-0.2.0.jar verify evergreen-retro-amendment
```

The `evergreen-retro-amendment` verifier models a retroactive payer policy
amendment and checks that REST-style authorization decisions, nightly queue
refresh behavior, checklist materialization, referral snapshot locking, audit,
and outbox behavior remain consistent.
