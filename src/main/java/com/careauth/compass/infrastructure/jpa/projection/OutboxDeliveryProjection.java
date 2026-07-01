package com.careauth.compass.infrastructure.jpa.projection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class OutboxDeliveryProjection {
    private static final String VIEW_NAME = "outbox_delivery_view";
    private static final List<String> COLUMNS = List.of("aggregate_id", "event_type", "policy_revision_id", "occurred_at");
    private static final List<String> INDEX_HINTS = List.of("idx_outbox_event", "idx_outbox_policy");

    public String viewName() {
        return VIEW_NAME;
    }

    public List<String> columns() {
        return COLUMNS;
    }

    public List<String> indexHints() {
        return INDEX_HINTS;
    }

    public boolean hasColumn(String column) {
        return COLUMNS.contains(column);
    }

    public Optional<String> preferredIndexFor(String column) {
        return INDEX_HINTS.stream()
                .filter(index -> index.toLowerCase().contains(column.toLowerCase()))
                .findFirst();
    }

    public Map<String, String> bindAliases(String prefix) {
        Map<String, String> aliases = new LinkedHashMap<>();
        for (String column : COLUMNS) {
            aliases.put(column, prefix + "_" + column);
        }
        return Map.copyOf(aliases);
    }

    public String selectClause(String alias) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < COLUMNS.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(alias).append('.').append(COLUMNS.get(i));
        }
        return builder.toString();
    }
}
