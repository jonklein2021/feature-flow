package com.featureflow.core.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Album {
    String id;
    String title;
    String artistId;
}
