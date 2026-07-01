# CareAuth Compass

CareAuth Compass is a simulated enterprise prior authorization platform for
referral intake, payer policy evaluation, clinical checklist generation, work
queue routing, audit observation, and outbox publishing.

The repository is intentionally large enough to look and feel like a real
service. It includes domain policies, Spring API boundaries, JPA-facing
infrastructure types, in-memory adapters used by scenario verification, and a
CLI verifier for payer policy incidents.

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
