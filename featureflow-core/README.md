# featureflow-core

The heart of the platform: the domain model, the declarative feature definition, the reference feature semantics, and the ports through which all infrastructure is reached. It has no dependency on any other module in this repository, and every other module depends on it.

## Place in the project

```text
featureflow-core  <---  featureflow-infra-local
       ^           <---  featureflow-generator
       |           <---  featureflow-app
```

If something must be true of a feature regardless of how or where it is computed, it belongs here. If it is specific to a technology (Kafka, Redis, Spark, files), it does not.

## What it contains

| Package | Contents |
|---------|----------|
| `domain` | `ListeningEvent`, `User`, `Song`, `Artist`, `Album`, `EventType` |
| `feature` | `Feature`, `FeatureRegistry`, `FeatureValue`, `FeatureEvaluator`, plus the vocabulary a definition is built from: `EntityType`, `Aggregation`, `EventField`, `DataType` |
| `spi` | Infrastructure ports: `EventStream`, `OfflineStore`, `OnlineStore` |

### Event identity and time

`ListeningEvent.eventId` is the identity of an event. Two events with the same id are the same event, and every consumer must treat the second as a duplicate. `timestamp` is event time (when the interaction happened), never ingestion time.

### Feature definitions

`Feature` is an immutable, engine-neutral description: entity, event type filter, aggregation, optional field, and window. The constructor validates it (positive window, field present when the aggregation needs one) and derives `dataType` from the aggregation. Currently supported aggregations are `COUNT`, `DISTINCT_COUNT`, and `SUM`, over `USER`, `SONG`, or `ARTIST` entities.

### Reference semantics

`FeatureEvaluator` defines what a feature means. Any offline or online engine must delegate to it or be tested for equivalence against it.

- The window is half open: `(asOf - window, asOf]`.
- Events after `asOf` never contribute, which prevents future leakage.
- Events sharing an `eventId` are counted once.

`FeatureEvaluatorTest` covers each of these rules, including point-in-time leakage, window boundaries, and duplicates.

## Current limitations

- `FeatureEvaluator` takes an in-memory collection of events for a single entity and cutoff. It is a correctness oracle, not a scalable engine.
- `FeatureRegistry` is only an interface here. The sole implementation is in-memory (see `featureflow-infra-local`).
- `DataType` has `DOUBLE` and `STRING` values, but no aggregation produces them yet.
- There is no port for the compute engine or the stream processor.

## Role in future phases

- **Phase 2 (feature definition and registry):** add a persistent `FeatureRegistry` implementation, richer metadata, and more aggregations. Features such as skip rate (a ratio), average duration, or favorite genre will need new `Aggregation` values and possibly new `EventField`s and result types.
- **Phase 3 (offline computation):** introduce a compute-engine port so the batch engine is replaceable, and make point-in-time dataset generation a first-class operation. `FeatureEvaluator` stays as the oracle that engine is tested against.
- **Phase 4 (online serving):** the stream processor and feature API build on `EventStream` and `OnlineStore`. Streaming state will likely need a state/checkpoint port.
- **Phase 5 (unified semantics):** the offline vs. online consistency checker compares engine output against `FeatureEvaluator` and against each other. Keep the evaluator authoritative.
- **Phase 6 (fault tolerance):** the duplicate-handling rule on `eventId` is the foundation of the delivery guarantees. Ports will need documented retry and failure behavior.
- **Phase 7 (discovery):** search, ownership, version history, and freshness metadata extend `Feature` and `FeatureRegistry`.
- **Phases 9 and 10:** embeddings and any new infrastructure kinds (embedding index, compute engine) should be added as new ports here, eachwith at least two implementations elsewhere.
