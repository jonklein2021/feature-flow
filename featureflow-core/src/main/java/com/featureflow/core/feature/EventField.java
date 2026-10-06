package com.featureflow.core.feature;

import com.featureflow.core.domain.ListeningEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/** an event attribute that a feature aggregation can read */
@RequiredArgsConstructor
public enum EventField {
    SONG_ID(ListeningEvent::getSongId),
    ARTIST_ID(ListeningEvent::getArtistId),
    DURATION_SECONDS(ListeningEvent::getDurationSeconds);

    private final Function<ListeningEvent, Object> extractor;

    /** returns the field value, or null when the event does not carry it */
    public Object extract(ListeningEvent event) {
        return extractor.apply(event);
    }
}
