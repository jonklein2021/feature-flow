package com.featureflow.app;

import com.featureflow.core.domain.ListeningEvent;
import com.featureflow.core.feature.FeatureEvaluator;
import com.featureflow.core.feature.FeatureRegistry;
import com.featureflow.core.spi.OfflineStore;
import com.featureflow.generator.SyntheticEventGenerator;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;

/**
 * end to end smoke path: generate events, register features, evaluate them for
 * one user
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DemoRunner {
    private static final long DEMO_USER_ID = 42;

    private final SyntheticEventGenerator generator;
    private final FeatureRegistry registry;
    private final OfflineStore offlineStore;
    private final FeatureEvaluator evaluator;

    public void run() {
        SampleFeatures.all().forEach(registry::register);

        List<ListeningEvent> events = generator.generateEvents(generator.generateCatalog()).toList();
        offlineStore.appendEvents(events);

        Instant asOf = events.get(events.size() - 1).getTimestamp();
        System.out.printf("generated %d events, evaluating user %d as of %s%n", events.size(), DEMO_USER_ID, asOf);

        registry.list().forEach(feature -> {
            Number value = evaluator.evaluate(feature, String.valueOf(DEMO_USER_ID), events, asOf);
            System.out.printf("  %s = %s%n", feature.getName(), value);
        });
    }
}
