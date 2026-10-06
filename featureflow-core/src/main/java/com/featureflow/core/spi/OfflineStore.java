package com.featureflow.core.spi;

import com.featureflow.core.domain.ListeningEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * durable, scan-oriented history used for batch feature computation (object
 * store, local files, ...)
 */
public interface OfflineStore {

    void appendEvents(Collection<ListeningEvent> events);

    /**
     * events with fromInclusive <= timestamp <= toInclusive, in no guaranteed order
     */
    Stream<ListeningEvent> scanEvents(Instant fromInclusive, Instant toInclusive);
}
