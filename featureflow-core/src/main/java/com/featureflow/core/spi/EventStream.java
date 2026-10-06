package com.featureflow.core.spi;

import com.featureflow.core.domain.ListeningEvent;
import java.util.function.Consumer;

/** transport for live listening events (kafka, in-memory queue, ...) */
public interface EventStream {

    void publish(ListeningEvent event);

    /**
     * registers a handler that receives every event published after subscription
     */
    void subscribe(Consumer<ListeningEvent> handler);
}
