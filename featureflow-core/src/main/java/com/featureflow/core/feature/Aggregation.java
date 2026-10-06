package com.featureflow.core.feature;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Aggregation {
    COUNT(false, DataType.LONG),
    DISTINCT_COUNT(true, DataType.LONG),
    SUM(true, DataType.LONG);

    /** whether the aggregation reads a specific event field */
    private final boolean requiresField;
    private final DataType resultType;
}
