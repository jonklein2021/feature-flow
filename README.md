# Feature Flow

An infrastructure-agnostic ML feature platform for music recommendation. Practitioners define features declaratively once, and the same definition is meant to drive both historical training datasets (offline) and low-latency serving (online), so training and serving cannot drift apart.

## Status

| Phase | Area | State |
|-------|------|-------|
| 1 | Domain model and synthetic event generator | Done (basic) |
| 2 | Feature definition API and registry | In-memory registry only, not persistent |
| 3 | Offline feature computation | Reference evaluator only, no batch engine yet |
| 4+ | Online serving, stream processing, consistency tooling, fault tolerance, ML | Not started |

## Requirements

- JDK 21 or newer
- Maven 3.9+

Homebrew's `mvn` launcher defaults `JAVA_HOME` to the latest `openjdk` formula when it is unset, which can differ from the `java` on your `PATH`. Check with `mvn -v`, and set `JAVA_HOME` explicitly if needed:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
```

## Build and test

```bash
mvn verify                                                              # build and test everything
mvn test -pl featureflow-core                                           # one module
mvn test -pl featureflow-core -Dtest=FeatureEvaluatorTest#methodName    # one test
```

## Run the demo

The demo generates 100,000 synthetic listening events, registers a few sample features, and evaluates them for user 42.

```bash
mvn install -DskipTests          # make module artifacts available to the app module
mvn -pl featureflow-app exec:java
```

Example output:

```text
generated 100000 events, evaluating user 42 as of 2026-09-14T23:59:21.120Z
  user_30d_unique_artist_count = 48
  user_24h_listen_time = 608
  user_7d_play_count = 8
```

Generator size and seed are controlled by `GeneratorConfig` (defaults: 1,000 users, 5,000 songs, 100,000 events over 45 days, seed 42). The same config always produces the same data.

## Project organization

```text
feature-flow/
├── featureflow-core/          domain model, feature definitions, evaluator, infrastructure ports
├── featureflow-infra-local/   in-memory implementations of the ports
├── featureflow-generator/     reproducible synthetic catalog and event generator
└── featureflow-app/           composition root (Dagger wiring) and demo entry point
```

- Dependencies point inward: `infra-local`, `generator`, and `app` depend on `core`
- `core` depends on no other module.

### featureflow-core

- `domain`: `ListeningEvent`, `User`, `Song`, `Artist`, `Album`, `EventType` (`PLAY`, `SKIP`, `LIKE`, `UNLIKE`, `ADD_TO_PLAYLIST`).
- `feature`: `Feature` (declarative definition), `FeatureRegistry`, `FeatureValue`, and `FeatureEvaluator`.
- `spi`: the infrastructure ports `EventStream`, `OfflineStore`, and `OnlineStore`.

### Feature semantics

`FeatureEvaluator` is the reference definition of what a feature means. Any offline or online engine must delegate to it, or be tested for equivalence against it.

- The window is half open: `(asOf - window, asOf]`.
- Events after `asOf` never contribute, which prevents future leakage into historical features.
- Events sharing an `eventId` are counted once, so duplicate delivery is harmless.

A feature is defined like this:

```java
Feature.builder()
        .name("user_7d_play_count")
        .entity(EntityType.USER)
        .eventType(EventType.PLAY)
        .aggregation(Aggregation.COUNT)
        .window(Duration.ofDays(7))
        .owner("recs-team")
        .description("number of plays in the trailing 7 days")
        .build();
```

Supported aggregations are `COUNT`, `DISTINCT_COUNT`, and `SUM`. More examples are in `featureflow-app/.../SampleFeatures.java`.

### Swapping infrastructure

The platform reaches infrastructure only through the ports in `core/spi`. `featureflow-app`'s `InfrastructureModule` is the single place that chooses concrete implementations, so adding a backend (for example Kafka or Redis) means writing a new module that implements a port and changing the binding there.

## Tech stack

Java 21, Maven multi-module, Lombok, Dagger 2 for dependency injection, JUnit 5 and AssertJ for tests.
