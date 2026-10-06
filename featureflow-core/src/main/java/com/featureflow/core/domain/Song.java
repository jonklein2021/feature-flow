package com.featureflow.core.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Song {
    String id;
    String title;
    String artistId;
    String albumId;
    String genre;
    int durationSeconds;
}
