package com.careauth.compass.domain.repository;

import com.careauth.compass.domain.model.AuditObservation;
import java.util.List;

public interface AuditRepository {
    void record(AuditObservation event);
    List<AuditObservation> findAll();
}
