package com.featureflow.app;

import com.featureflow.core.feature.FeatureRegistry;
import com.featureflow.core.spi.EventStream;
import com.featureflow.core.spi.OfflineStore;
import com.featureflow.core.spi.OnlineStore;
import com.featureflow.infra.local.InMemoryEventStream;
import com.featureflow.infra.local.InMemoryFeatureRegistry;
import com.featureflow.infra.local.InMemoryOfflineStore;
import com.featureflow.infra.local.InMemoryOnlineStore;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** the one place that chooses concrete infrastructure; swap implementations here */
@Module
public class InfrastructureModule {

    @Provides
    @Singleton
    EventStream eventStream() {
        return new InMemoryEventStream();
    }

    @Provides
    @Singleton
    OfflineStore offlineStore() {
        return new InMemoryOfflineStore();
    }

    @Provides
    @Singleton
    OnlineStore onlineStore() {
        return new InMemoryOnlineStore();
    }

    @Provides
    @Singleton
    FeatureRegistry featureRegistry() {
        return new InMemoryFeatureRegistry();
    }
}
