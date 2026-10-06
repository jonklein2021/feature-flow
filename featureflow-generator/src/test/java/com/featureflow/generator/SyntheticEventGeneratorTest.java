package com.featureflow.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SyntheticEventGeneratorTest {

    private final GeneratorConfig config = GeneratorConfig.builder().userCount(50).songCount(200).eventCount(1_000)
            .build();

    @Test
    void sameSeedProducesIdenticalEvents() {
        var first = new SyntheticEventGenerator(config);
        var second = new SyntheticEventGenerator(config);

        var firstEvents = first.generateEvents(first.generateCatalog()).toList();
        var secondEvents = second.generateEvents(second.generateCatalog()).toList();

        assertThat(firstEvents).hasSize(1_000).isEqualTo(secondEvents);
    }

    @Test
    void eventsAreInTimestampOrderWithUniqueIds() {
        var generator = new SyntheticEventGenerator(config);

        var events = generator.generateEvents(generator.generateCatalog()).toList();

        assertThat(events).extracting(event -> event.getEventId()).doesNotHaveDuplicates();
        assertThat(events).extracting(event -> event.getTimestamp()).isSorted();
    }
}
