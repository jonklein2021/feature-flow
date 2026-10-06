package com.featureflow.core.feature;

import java.util.List;
import java.util.Optional;

/** catalog of feature definitions, keyed by name and version */
public interface FeatureRegistry {

    /** registers a feature; fails if the same name and version already exists */
    void register(Feature feature);

    /** latest version of the named feature */
    Optional<Feature> find(String name);

    Optional<Feature> find(String name, int version);

    /** all versions of the named feature, oldest first */
    List<Feature> versions(String name);

    /** latest version of every registered feature */
    List<Feature> list();

    List<Feature> listByEntity(EntityType entity);
}
