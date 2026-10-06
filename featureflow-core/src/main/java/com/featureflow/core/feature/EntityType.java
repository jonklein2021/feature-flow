package com.featureflow.core.feature;

import com.featureflow.core.domain.ListeningEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/** the thing a feature describes, and how to find that thing's key on an event */
@RequiredArgsConstructor
public enum EntityType {
    USER(event -> String.valueOf(event.getUserId())),
    SONG(ListeningEvent::getSongId),
    ARTIST(ListeningEvent::getArtistId);

    private final Function<ListeningEvent, String> keyExtractor;

    public String keyOf(ListeningEvent event) {
        return keyExtractor.apply(event);
    }
}
