package com.featureflow.infra.local;

import com.featureflow.core.domain.ListeningEvent;
import com.featureflow.core.spi.EventStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** synchronous in-process stream; handlers run on the publishing thread */
public class InMemoryEventStream implements EventStream {

    private final List<Consumer<ListeningEvent>> handlers = new CopyOnWriteArrayList<>();

    @Override
    public void publish(ListeningEvent event) {
        handlers.forEach(handler -> handler.accept(event));
    }

    @Override
    public void subscribe(Consumer<ListeningEvent> handler) {
        handlers.add(handler);
    }
}
