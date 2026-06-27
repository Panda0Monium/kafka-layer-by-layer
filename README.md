# Down the Stack: Kafka in Nine Layers — Companion Code

Companion repository for the [Down the Stack: Kafka in Nine Layers](https://medium.com/@karanpatel2193/down-the-stack-kafka-in-nine-layers-65541826dd65) series. Each layer has its own runnable Spring Boot application under a numbered directory.

## Structure

| Directory | Post |
|-----------|------|
| `layer1/` | [Layer 1: Track Page Views](https://medium.com/@karanpatel2193/layer-1-track-page-views-bbac54dc88e9) |
| `layer2/` | Layer 2: The App Gets Popular *(coming soon)* |

More layers will be added as the series publishes.

## Running a layer

Each layer is self-contained. From the `layer<N>/` directory:

```bash
docker compose up --build
```

Kafka, MySQL, and the application start together. Each layer's `scripts/` directory has shell scripts for sending events and observing behavior.

## Tech stack

Java 21, Spring Boot 3, Spring Kafka, MySQL 8, Kafka (KRaft mode). Tests use Testcontainers.
