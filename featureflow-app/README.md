# featureflow-app

The composition root. It selects concrete infrastructure, wires the platform together with Dagger, and provides the runnable entry point. It is the only module that depends on all the others, and nothing depends on it.

## Place in the project

```text
featureflow-app --> featureflow-core
                --> featureflow-infra-local
                --> featureflow-generator
```

The platform logic lives in `featureflow-core`. This module decides which implementations of its ports are used, which is what keeps the platform infrastructure-agnostic in practice.

## Structure

| Class | Purpose |
|-------|---------|
| `InfrastructureModule` | The single place that chooses concrete `EventStream`, `OfflineStore`, `OnlineStore`, and `FeatureRegistry` implementations. Swap a backend here. |
| `PlatformModule` | Bindings that do not depend on a specific infrastructure: generator config, generator, `FeatureEvaluator`. |
| `FeatureFlowComponent` | The Dagger component exposing what the app needs. |
| `DemoRunner` | End-to-end smoke path: generate events, register features, store events, evaluate features for one user. |
| `SampleFeatures` | Example feature definitions used by the demo. |
| `FeatureFlowApp` | `main`. |

## Running

```bash
mvn install -DskipTests          # from the repository root
mvn -pl featureflow-app exec:java
```

The install step is needed because `exec:java` runs against this module alone and resolves its sibling modules from the local Maven repository.

The demo prints the generated event count and the values of the sample features for user 42, for example:

```text
generated 100000 events, evaluating user 42 as of 2026-09-14T23:59:21.120Z
  user_30d_unique_artist_count = 48
  user_24h_listen_time = 608
  user_7d_play_count = 8
```

## Build notes

- Annotation processors are ordered so Lombok runs before the Dagger compiler, because Dagger needs the constructors Lombok generates. Keep that order if editing the pom.
- `DaggerFeatureFlowComponent` is generated at compile time, so build before expecting an IDE to resolve it.
- Classes use `@RequiredArgsConstructor(onConstructor_ = @Inject)` for constructor injection.

## Current limitations

The demo is a single synchronous script. Nothing is served, no events flow through an `EventStream`, `OnlineStore` is bound but unused, and features are evaluated directly with the reference evaluator.

## Role in future phases

- **Offline Feature Computation:** the batch pipeline and a historical dataset generation entry point will be wired and exposed from here.
- **Online Feature Computation and Serving:** the stream processor and the feature-serving API (`GET /features/user/42`) will start here, with `EventStream`, `OnlineStore`, and the processor connected through new Dagger bindings.
- **Unified Offline and Online Semantics:** the offline vs. online consistency check will be run as part of the demo or as a command here.
- **Fault Tolerance and Recovery:** the failure and recovery scenarios (kill a worker, restart the store, duplicate or reorder events) need an entry point that can start, stop, and restart components, which is naturally this module's job.
- **Feature Discovery and ML Workflow:** the practitioner workflow and the recommendation training and inference path are driven from here, with the Python notebook client talking to the API started by this module.
- **Infrastructure Abstraction:** the demonstration of swapping infrastructure is a change in `InfrastructureModule`, possibly selected by configuration. The same feature definitions then run unchanged on different backends.
- **Cloud, Observability and Benchmarking:** container images, deployment configuration, metrics export, and benchmark runners attach to this module's runtime.
