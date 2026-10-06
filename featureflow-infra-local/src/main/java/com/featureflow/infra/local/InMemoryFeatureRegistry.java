package com.featureflow.infra.local;

import com.featureflow.core.feature.EntityType;
import com.featureflow.core.feature.Feature;
import com.featureflow.core.feature.FeatureRegistry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryFeatureRegistry implements FeatureRegistry {

    private final Map<String, ConcurrentSkipListMap<Integer, Feature>> featuresByName = new ConcurrentHashMap<>();

    @Override
    public void register(Feature feature) {
        var versions = featuresByName.computeIfAbsent(feature.getName(), name -> new ConcurrentSkipListMap<>());
        if (versions.putIfAbsent(feature.getVersion(), feature) != null) {
            throw new IllegalStateException(
                    "feature already registered: " + feature.getName() + " v" + feature.getVersion());
        }
    }

    @Override
    public Optional<Feature> find(String name) {
        var versions = featuresByName.get(name);
        return versions == null ? Optional.empty() : Optional.of(versions.lastEntry().getValue());
    }

    @Override
    public Optional<Feature> find(String name, int version) {
        var versions = featuresByName.get(name);
        return versions == null ? Optional.empty() : Optional.ofNullable(versions.get(version));
    }

    @Override
    public List<Feature> versions(String name) {
        var versions = featuresByName.get(name);
        return versions == null ? List.of() : List.copyOf(versions.values());
    }

    @Override
    public List<Feature> list() {
        return featuresByName.values().stream()
                .map(versions -> versions.lastEntry().getValue())
                .toList();
    }

    @Override
    public List<Feature> listByEntity(EntityType entity) {
        return list().stream().filter(feature -> feature.getEntity() == entity).toList();
    }
}
