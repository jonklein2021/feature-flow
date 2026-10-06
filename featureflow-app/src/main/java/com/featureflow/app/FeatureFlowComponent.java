package com.featureflow.app;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = { InfrastructureModule.class, PlatformModule.class })
public interface FeatureFlowComponent {

    DemoRunner demoRunner();
}
