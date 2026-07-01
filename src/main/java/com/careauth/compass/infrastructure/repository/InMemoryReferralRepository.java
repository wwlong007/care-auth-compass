package com.careauth.compass.infrastructure.repository;

import com.careauth.compass.domain.model.Referral;
import com.careauth.compass.domain.repository.ReferralRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryReferralRepository implements ReferralRepository {
    private final Map<String, Referral> referrals = new LinkedHashMap<>();

    public InMemoryReferralRepository(List<Referral> referrals) {
        for (Referral referral : referrals) {
            this.referrals.put(referral.id(), referral);
        }
    }

    @Override
    public Optional<Referral> findById(String referralId) {
        return Optional.ofNullable(referrals.get(referralId));
    }

    @Override
    public List<Referral> findAll() {
        return List.copyOf(referrals.values());
    }

    @Override
    public void save(Referral referral) {
        referrals.put(referral.id(), referral);
    }
}
