package com.featureflow.app;

import com.featureflow.core.domain.EventType;
import com.featureflow.core.feature.Aggregation;
import com.featureflow.core.feature.EntityType;
import com.featureflow.core.feature.EventField;
import com.featureflow.core.feature.Feature;
import java.time.Duration;
import java.util.List;

/** example feature definitions used by the demo */
final class SampleFeatures {

        private SampleFeatures() {
        }

        static List<Feature> all() {
                return List.of(
                                Feature.builder()
                                                .name("user_7d_play_count")
                                                .entity(EntityType.USER)
                                                .eventType(EventType.PLAY)
                                                .aggregation(Aggregation.COUNT)
                                                .window(Duration.ofDays(7))
                                                .owner("recs-team")
                                                .description("number of plays in the trailing 7 days")
                                                .build(),
                                Feature.builder()
                                                .name("user_30d_unique_artist_count")
                                                .entity(EntityType.USER)
                                                .eventType(EventType.PLAY)
                                                .aggregation(Aggregation.DISTINCT_COUNT)
                                                .field(EventField.ARTIST_ID)
                                                .window(Duration.ofDays(30))
                                                .owner("recs-team")
                                                .description("distinct artists played in the trailing 30 days")
                                                .build(),
                                Feature.builder()
                                                .name("user_24h_listen_time")
                                                .entity(EntityType.USER)
                                                .eventType(EventType.PLAY)
                                                .aggregation(Aggregation.SUM)
                                                .field(EventField.DURATION_SECONDS)
                                                .window(Duration.ofHours(24))
                                                .owner("recs-team")
                                                .description("seconds of listening in the trailing 24 hours")
                                                .build());
        }
}
