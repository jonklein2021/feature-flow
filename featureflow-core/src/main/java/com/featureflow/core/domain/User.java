package com.featureflow.core.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class User {
    long id;
    String country;
    Instant createdAt;
}
