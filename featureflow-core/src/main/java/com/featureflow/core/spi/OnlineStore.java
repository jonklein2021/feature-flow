package com.featureflow.core.spi;

import com.featureflow.core.feature.EntityType;
import com.featureflow.core.feature.FeatureValue;
import java.util.Collection;
import java.util.Map;

/**
 * low latency key-value access to the latest feature values (redis, in-memory
 * map, ...)
 */
public interface OnlineStore {

    void put(EntityType entity, String entityKey, FeatureValue value);

    /** returns the requested features that exist, keyed by feature name */
    Map<String, FeatureValue> get(EntityType entity, String entityKey, Collection<String> featureNames);
}
