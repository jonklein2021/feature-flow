package com.featureflow.core.feature;

import java.time.Instant;
import lombok.Value;

/** a computed feature value for one entity at one point in time */
@Value
public class FeatureValue {
    String featureName;
    int featureVersion;
    Number value;

    /** the cutoff the value was computed for; only events at or before this instant contributed */
    Instant asOf;
}
