package com.careauth.compass.infrastructure.reporting.projection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ClinicalReviewerReportProjection {
    private static final String VIEW_NAME = "clinical_reviewer_view";
    private static final List<String> COLUMNS = List.of("tenant_id", "clinician_id", "specialty", "queue_name");
    private static final List<String> INDEX_HINTS = List.of("idx_reviewer_specialty", "idx_reviewer_queue");

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
