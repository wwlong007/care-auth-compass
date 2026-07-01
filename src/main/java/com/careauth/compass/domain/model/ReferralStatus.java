package com.careauth.compass.domain.model;

public enum ReferralStatus {
    DRAFT,
    PENDING_AUTH,
    PENDING_DOCS,
    CLINICAL_REVIEW,
    AUTH_APPROVED,
    SCHEDULED,
    COMPLETED,
    CANCELLED;


    public boolean isMutableForRetroReevaluation() {
        return this == PENDING_AUTH || this == PENDING_DOCS || this == CLINICAL_REVIEW;
    }

    public boolean isLockedForRetroReevaluation() {
        return this == AUTH_APPROVED || this == SCHEDULED || this == COMPLETED || this == CANCELLED;
    }

}
