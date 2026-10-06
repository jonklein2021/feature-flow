# featureflow-generator

A reproducible generator for synthetic catalog data (users, artists, albums, songs) and listening events. It is the platform's only data source until real ingestion exists.

## Place in the project

This module depends only on `featureflow-core`, using its domain types (`User`, `Artist`, `Album`, `Song`, `ListeningEvent`, `EventType`). The platform itself never depends on it. It is tooling that produces input for the platform, wired in by `featureflow-app`.

## Usage

```java
var generator = new SyntheticEventGenerator(GeneratorConfig.builder().build());
Catalog catalog = generator.generateCatalog();
Stream<ListeningEvent> events = generator.generateEvents(catalog);
```

`GeneratorConfig` defaults:

| Setting | Default |
|---------|---------|
| `seed` | 42 |
| `userCount` | 1,000 |
| `artistCount` | 200 |
| `songCount` | 5,000 |
| `eventCount` | 100,000 |
| `startTime` | 2026-08-01T00:00:00Z |
| `span` | 45 days |

## Guarantees

- **Reproducible:** the same config always produces the same catalog and events. Any new randomness must be derived from `GeneratorConfig.seed`.
- **Lazy:** `generateEvents` returns a stream, so large event counts are not held in memory by the generator. The stream is stateful and single-use. Call the method again for a fresh, identical sequence.
- **Ordered with unique ids:** events come out in strictly increasing timestamp order, each with a distinct `eventId`. `SyntheticEventGeneratorTest` checks both this and reproducibility.

## Current limitations

The model is deliberately simple:

- Users and songs are picked uniformly, so there is no popularity skew or per-user taste.
- Event type mix is fixed (65% `PLAY`, 20% `SKIP`, 8% `LIKE`, 3% `UNLIKE`, 4% `ADD_TO_PLAYLIST`) and independent of user or song.
- Timestamps are evenly spaced, with no sessions, daily rhythms, or bursts.
- No duplicate events, late events, or out-of-order delivery.
- No playlist entities.

## Role in future phases

- **Phase 1 (finishing):** make the data realistic: skewed popularity, per-user genre preferences, listening sessions, and scale toward the 10,000 users / 100,000 songs / 10,000,000 events range. Real-world imperfection (duplicates, late events, out-of-order arrival) should be optional, seeded knobs on `GeneratorConfig`.
- **Phases 3 and 5:** reproducible data lets offline results and offline-vs-online comparisons be repeated exactly. Preference structure in the data also determines whether the features carry any signal.
- **Phase 4 and 6:** the lazy stream can be paced and published into an `EventStream` to simulate live traffic, and the imperfection knobs drive duplicate and out-of-order failure tests.
- **Phase 8 and 9:** the recommendation model and embeddings are only meaningful if the generated behavior has learnable structure (for example genre affinity and co-listening), which is currently absent.
- **Phase 12:** the configurable event count and rate are the load source for throughput benchmarks. Generating in a streaming fashion keepsbenchmark memory use independent of dataset size.
