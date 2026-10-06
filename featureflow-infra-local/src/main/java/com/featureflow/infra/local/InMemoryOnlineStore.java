package com.featureflow.infra.local;

import com.featureflow.core.feature.EntityType;
import com.featureflow.core.feature.FeatureValue;
import com.featureflow.core.spi.OnlineStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOnlineStore implements OnlineStore {

    private final Map<String, Map<String, FeatureValue>> rows = new ConcurrentHashMap<>();

    @Override
    public void put(EntityType entity, String entityKey, FeatureValue value) {
        rows.computeIfAbsent(rowKey(entity, entityKey), key -> new ConcurrentHashMap<>())
                .put(value.getFeatureName(), value);
    }

    @Override
    public Map<String, FeatureValue> get(EntityType entity, String entityKey, Collection<String> featureNames) {
        var row = rows.getOrDefault(rowKey(entity, entityKey), Map.of());
        var result = new HashMap<String, FeatureValue>();
        for (String name : featureNames) {
            FeatureValue value = row.get(name);
            if (value != null) {
                result.put(name, value);
            }
        }
        return result;
    }

    private static String rowKey(EntityType entity, String entityKey) {
        return entity + ":" + entityKey;
    }
}
