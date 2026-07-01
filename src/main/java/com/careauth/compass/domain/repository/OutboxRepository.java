package com.careauth.compass.domain.repository;

import com.careauth.compass.domain.model.OutboxEvent;
import java.util.List;

public interface OutboxRepository {
    void publish(OutboxEvent event);
    List<OutboxEvent> findAll();
}
