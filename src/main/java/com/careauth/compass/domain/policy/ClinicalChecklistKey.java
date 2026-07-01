package com.careauth.compass.domain.policy;

import com.careauth.compass.domain.model.Referral;

public record ClinicalChecklistKey(
        String referralId,
        String procedureCode
) {
    public static ClinicalChecklistKey of(Referral referral, String policyRevisionId) {
        return new ClinicalChecklistKey(referral.id(), referral.procedureCode());
    }
}
