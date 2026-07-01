package com.careauth.compass.domain.repository;

import com.careauth.compass.domain.model.Referral;
import java.util.List;
import java.util.Optional;

public interface ReferralRepository {
    Optional<Referral> findById(String referralId);
    List<Referral> findAll();
    void save(Referral referral);

    default Referral require(String referralId) {
        return findById(referralId).orElseThrow(() -> new IllegalArgumentException("Unknown referral " + referralId));
    }

    default List<Referral> findOpenForQueueRefresh() {
        return findAll().stream()
                .filter(referral -> !referral.status().isLockedForRetroReevaluation())
                .toList();
    }
}
