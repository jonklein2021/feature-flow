package com.featureflow.core.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

/**
 * a single listener interaction.
 *
 * <p>{@code eventId} is the identity of an event: two events with the same id are the same event
 * and must be treated as duplicates by every consumer. {@code timestamp} is event time (when the
 * interaction happened), not ingestion time.
 */
@Value
@Builder
public class ListeningEvent {
    String eventId;
    long userId;
    String songId;
    String artistId;
    EventType type;
    Instant timestamp;

    /** listened seconds for PLAY and SKIP, null for event types without a duration */
    Integer durationSeconds;
}
