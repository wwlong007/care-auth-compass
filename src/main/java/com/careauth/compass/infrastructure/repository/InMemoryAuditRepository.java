package com.careauth.compass.infrastructure.repository;

import com.careauth.compass.domain.model.AuditObservation;
import com.careauth.compass.domain.repository.AuditRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuditRepository implements AuditRepository {
    private final List<AuditObservation> observations = new ArrayList<>();

    @Override
    public void record(AuditObservation event) {
        observations.add(event);
    }

    @Override
    public List<AuditObservation> findAll() {
        return List.copyOf(observations);
    }
}
