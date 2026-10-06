# featureflow-infra-local

In-process, in-memory implementations of every infrastructure port defined in `featureflow-core`. This is what makes the platform runnable with no containers, cloud account, or external services.

## Place in the project

This module depends only on `featureflow-core` and implements its interfaces:

| Port (in core) | Implementation |
|----------------|----------------|
| `EventStream` | `InMemoryEventStream` |
| `OfflineStore` | `InMemoryOfflineStore` |
| `OnlineStore` | `InMemoryOnlineStore` |
| `FeatureRegistry` | `InMemoryFeatureRegistry` |

Nothing else in the platform references these classes. Only `featureflow-app`'s `InfrastructureModule` chooses them, so replacing any of them is a one-binding change.

## Behavior of each implementation

- **`InMemoryEventStream`** is synchronous. `publish` invokes every subscribed handler on the calling thread. Handlers only see events published after they subscribe. There is no buffering, replay, partitioning, or ordering guarantee beyond call order.
- **`InMemoryOfflineStore`** appends to a queue and answers `scanEvents` with a linear filter over the time range. It does not deduplicate or order events, matching the port's contract, so consumers must handle duplicates (the evaluator does).
- **`InMemoryOnlineStore`** is a concurrent map keyed by entity type and key, then feature name. `put` overwrites the previous value without comparing `asOf`.
- **`InMemoryFeatureRegistry`** keeps every version of each feature, rejects re-registering the same name and version, and returns the highest version as the latest.

All state is lost when the process exits, and none of it is bounded by memory limits.

## Role in future phases

This module is the baseline implementation that real infrastructure is compared against and swapped with.

- **Phase 2:** a persistent registry (for example file or database backed) will sit beside `InMemoryFeatureRegistry`.
- **Phase 3:** a file-based `OfflineStore` is the natural first alternative to the in-memory queue, and scans will need to stop being linear in total history.
- **Phase 4:** the online path needs an `OnlineStore` that can handle concurrent updates. Deciding whether `put` should ignore older `asOf` values belongs to this phase and to Phase 6.
- **Phase 6:** these implementations are the easy place to inject failures (dropped, duplicated, or reordered events; an unavailable store) for the recovery test suite.
- **Phase 10 (infrastructure abstraction):** each port needs at least two implementations. The in-memory ones count as one half of each pair; the other half (for example Kafka for `EventStream`, Redis for `OnlineStore`) would live in new sibling modules such as `featureflow-infra-kafka` rather than in this one. This module should stay free of third-party infrastructure dependencies.
- **Phase 12:** it is useful as a zero-latency control when benchmarking, to separate platform overhead from backend cost.
