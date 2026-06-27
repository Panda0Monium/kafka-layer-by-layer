# Layer 2: The App Gets Popular

Twelve partitions. Session-keyed routing. Three consumer groups reading the same topic independently.

The topic grows from 1 partition to 12 to absorb a 40x traffic spike. The `send()` call gains a key: `session_id`. All events for the same session land on the same partition in order — the guarantee the funnel team needs. Three consumer groups (`page-counter`, `live-dashboard`, `signup-alerts`) each read at their own pace without coordinating.

## Stack

| Component | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Kafka | 3.3.x (managed by Boot) |
| Kafka | Confluent cp-kafka 7.9 (KRaft, single broker) |
| MySQL | 8.0 |

## Run

```bash
# from companion/
docker compose -f layer2/docker-compose.yml up --build
```

Send events (three sessions, one signup, one anonymous):

```bash
./layer2/scripts/send-events.sh
```

Check page counts:

```bash
curl http://localhost:8080/counts
```

Stream the live dashboard over SSE:

```bash
curl -N http://localhost:8080/dashboard
```

Single event with an explicit session key:

```bash
curl -X POST http://localhost:8080/track \
  -H "Content-Type: application/json" \
  -d '{"event_type":"page_view","session_id":"sess-abc","url":"/pricing"}'
```

Omit `session_id` and the server generates a UUID key:

```bash
curl -X POST http://localhost:8080/track \
  -H "Content-Type: application/json" \
  -d '{"event_type":"page_view","url":"/home"}'
```

## Test

```bash
# from companion/
./gradlew :layer2:test
```

## Inspect partitions and consumer lag

Shell into the Kafka container:

```bash
docker compose -f layer2/docker-compose.yml exec kafka bash
```

Then inside:

```bash
# 12 partitions, 1 replica
kafka-topics --bootstrap-server localhost:9092 --describe --topic events

# lag across all three consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --all-groups
```

## Demonstrate partition-to-consumer assignment

The post shows four page-counter instances each owning three partitions. To reproduce:

1. In `layer2/docker-compose.yml`, change `"8080:8080"` to `"8080"` under `app.ports` (avoids host port conflicts when scaling).
2. Scale to four instances:

```bash
docker compose -f layer2/docker-compose.yml up --build --scale app=4
```

3. The app containers no longer bind a fixed host port, so find the ephemeral port for one instance and use that to send events:

```bash
docker compose -f layer2/docker-compose.yml port app 8080
# e.g. outputs 0.0.0.0:54321

./layer2/scripts/send-events.sh http://localhost:54321
```

All four consumers receive events from Kafka regardless of which instance the producer hit.

4. Shell into Kafka and check the `page-counter` group: four members, three partitions each.

```bash
docker compose -f layer2/docker-compose.yml exec kafka bash
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group page-counter
```

## What's next

Layer 3 adds Avro and Schema Registry. The schema for `PageEvent` looks like a reasonable first pass.
