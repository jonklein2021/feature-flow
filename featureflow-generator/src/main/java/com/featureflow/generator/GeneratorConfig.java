package com.featureflow.generator;

import java.time.Duration;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeneratorConfig {

    /** same seed and config always yields the same catalog and events */
    @Builder.Default
    long seed = 42L;

    @Builder.Default
    int userCount = 1_000;

    @Builder.Default
    int artistCount = 200;

    @Builder.Default
    int songCount = 5_000;

    @Builder.Default
    long eventCount = 100_000L;

    @Builder.Default
    Instant startTime = Instant.parse("2026-08-01T00:00:00Z");

    @Builder.Default
    Duration span = Duration.ofDays(45);
}
