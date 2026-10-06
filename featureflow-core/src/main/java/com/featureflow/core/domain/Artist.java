package com.featureflow.core.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Artist {
    String id;
    String name;
}
