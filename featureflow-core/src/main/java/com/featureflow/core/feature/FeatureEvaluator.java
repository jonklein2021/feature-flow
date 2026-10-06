package com.featureflow.core.feature;

import com.featureflow.core.domain.ListeningEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * reference semantics for feature definitions. the offline and online engines
 * must both delegate
 * to this class (or be tested for equivalence against it) so training and
 * serving cannot drift.
 *
 * <p>
 * rules:
 * <ul>
 * <li>the window is half open: (asOf - window, asOf], so an event exactly at
 * asOf counts and an
 * event exactly at the window start does not
 * <li>events after asOf never contribute, which is what prevents future leakage
 * <li>events sharing an eventId are counted once
 * </ul>
 */
public final class FeatureEvaluator {

    /** computes the value of a feature for one entity as of the given cutoff */
    public Number evaluate(
            Feature feature, String entityKey, Collection<ListeningEvent> events, Instant asOf) {
        Instant windowStart = asOf.minus(feature.getWindow());

        Map<String, ListeningEvent> unique = new LinkedHashMap<>();
        for (ListeningEvent event : events) {
            unique.putIfAbsent(event.getEventId(), event);
        }

        var matching = unique.values().stream()
                .filter(event -> entityKey.equals(feature.getEntity().keyOf(event)))
                .filter(event -> feature.getEventType() == null || event.getType() == feature.getEventType())
                .filter(event -> event.getTimestamp().isAfter(windowStart))
                .filter(event -> !event.getTimestamp().isAfter(asOf))
                .toList();

        return switch (feature.getAggregation()) {
            case COUNT -> (long) matching.size();
            case DISTINCT_COUNT -> matching.stream()
                    .map(feature.getField()::extract)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();
            case SUM -> matching.stream()
                    .map(feature.getField()::extract)
                    .filter(Objects::nonNull)
                    .mapToLong(value -> ((Number) value).longValue())
                    .sum();
        };
    }
}
