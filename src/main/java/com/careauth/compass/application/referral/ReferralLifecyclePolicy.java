package com.careauth.compass.application.referral;

import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.model.ReferralStatus;

public class ReferralLifecyclePolicy {
    public boolean mayOverwriteRetroSnapshot(Referral referral) {
        return referral.status() != ReferralStatus.COMPLETED
                && referral.status() != ReferralStatus.CANCELLED;
    }
}
