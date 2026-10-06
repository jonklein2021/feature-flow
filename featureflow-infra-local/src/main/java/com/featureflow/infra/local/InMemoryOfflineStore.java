package com.featureflow.infra.local;

import com.featureflow.core.domain.ListeningEvent;
import com.featureflow.core.spi.OfflineStore;
import java.time.Instant;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class InMemoryOfflineStore implements OfflineStore {

    private final Queue<ListeningEvent> events = new ConcurrentLinkedQueue<>();

    @Override
    public void appendEvents(Collection<ListeningEvent> batch) {
        events.addAll(batch);
    }

    @Override
    public Stream<ListeningEvent> scanEvents(Instant fromInclusive, Instant toInclusive) {
        return events.stream()
                .filter(event -> !event.getTimestamp().isBefore(fromInclusive))
                .filter(event -> !event.getTimestamp().isAfter(toInclusive));
    }
}
