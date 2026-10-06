package com.featureflow.core.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.featureflow.core.domain.EventType;
import com.featureflow.core.domain.ListeningEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeatureEvaluatorTest {

    private static final Instant NOON = Instant.parse("2026-09-15T12:00:00Z");

    private final FeatureEvaluator evaluator = new FeatureEvaluator();

    private final Feature playCount7d = Feature.builder()
            .name("user_7d_play_count")
            .entity(EntityType.USER)
            .eventType(EventType.PLAY)
            .aggregation(Aggregation.COUNT)
            .window(Duration.ofDays(7))
            .build();

    @Test
    void futureEventsDoNotLeakIntoHistoricalValues() {
        var events = List.of(
                play("e1", 42, "s1", NOON.minusSeconds(60)),
                play("e2", 42, "s1", NOON.plusSeconds(5 * 60)),
                play("e3", 42, "s1", NOON.plusSeconds(30 * 60)),
                play("e4", 42, "s1", NOON.plusSeconds(2 * 3600)));

        assertThat(evaluator.evaluate(playCount7d, "42", events, NOON)).isEqualTo(1L);
    }

    @Test
    void windowIsHalfOpenAtTheStartAndInclusiveAtTheCutoff() {
        var events = List.of(
                play("atStart", 42, "s1", NOON.minus(Duration.ofDays(7))),
                play("atCutoff", 42, "s1", NOON));

        assertThat(evaluator.evaluate(playCount7d, "42", events, NOON)).isEqualTo(1L);
    }

    @Test
    void duplicateEventsAreCountedOnce() {
        var event = play("e1", 42, "s1", NOON.minusSeconds(10));

        assertThat(evaluator.evaluate(playCount7d, "42", List.of(event, event), NOON)).isEqualTo(1L);
    }

    @Test
    void onlyTheRequestedEntityIsCounted() {
        var events = List.of(play("e1", 42, "s1", NOON.minusSeconds(10)), play("e2", 17, "s1", NOON.minusSeconds(10)));

        assertThat(evaluator.evaluate(playCount7d, "17", events, NOON)).isEqualTo(1L);
    }

    @Test
    void featureWithoutRequiredFieldIsRejected() {
        assertThatThrownBy(() -> Feature.builder()
                .name("bad")
                .entity(EntityType.USER)
                .aggregation(Aggregation.SUM)
                .window(Duration.ofDays(1))
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static ListeningEvent play(String id, long userId, String songId, Instant at) {
        return ListeningEvent.builder()
                .eventId(id)
                .userId(userId)
                .songId(songId)
                .artistId("a1")
                .type(EventType.PLAY)
                .timestamp(at)
                .durationSeconds(180)
                .build();
    }
}
