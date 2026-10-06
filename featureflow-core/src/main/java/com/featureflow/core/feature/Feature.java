package com.featureflow.core.feature;

import com.featureflow.core.domain.EventType;
import java.time.Duration;
import java.util.Objects;
import lombok.Builder;
import lombok.Value;

/**
 * declarative feature definition. this is the single source of truth that both the offline and
 * online engines evaluate, so it must not contain anything engine specific.
 */
@Value
public class Feature {
    String name;
    int version;
    EntityType entity;
    DataType dataType;
    String source;

    /** restricts the feature to one event type, null means all event types */
    EventType eventType;

    Aggregation aggregation;

    /** the field read by the aggregation, null for aggregations that do not need one */
    EventField field;

    Duration window;
    String owner;
    String description;

    @Builder
    private Feature(
            String name,
            Integer version,
            EntityType entity,
            String source,
            EventType eventType,
            Aggregation aggregation,
            EventField field,
            Duration window,
            String owner,
            String description) {
        this.name = requireNonBlank(name, "name");
        this.version = version == null ? 1 : version;
        this.entity = Objects.requireNonNull(entity, "entity");
        this.aggregation = Objects.requireNonNull(aggregation, "aggregation");
        this.window = Objects.requireNonNull(window, "window");
        this.source = source == null ? "listening_events" : source;
        this.eventType = eventType;
        this.field = field;
        this.owner = owner;
        this.description = description;
        this.dataType = aggregation.getResultType();

        if (this.version < 1) {
            throw new IllegalArgumentException("version must be >= 1");
        }
        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be positive");
        }
        if (aggregation.isRequiresField() && field == null) {
            throw new IllegalArgumentException(aggregation + " requires a field");
        }
    }

    private static String requireNonBlank(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value;
    }
}
