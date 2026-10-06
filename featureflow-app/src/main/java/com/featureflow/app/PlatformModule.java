package com.featureflow.app;

import com.featureflow.core.feature.FeatureEvaluator;
import com.featureflow.generator.GeneratorConfig;
import com.featureflow.generator.SyntheticEventGenerator;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * bindings for platform logic and tooling that do not depend on a specific
 * infrastructure
 */
@Module
public class PlatformModule {

    @Provides
    @Singleton
    GeneratorConfig generatorConfig() {
        return GeneratorConfig.builder().build();
    }

    @Provides
    @Singleton
    SyntheticEventGenerator eventGenerator(GeneratorConfig config) {
        return new SyntheticEventGenerator(config);
    }

    @Provides
    @Singleton
    FeatureEvaluator featureEvaluator() {
        return new FeatureEvaluator();
    }
}
