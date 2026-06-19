# Layer 1 — Track Page Views

One topic. One partition. No keys. Raw JSON.

A POST to `/track` produces an event; a `@KafkaListener` reads it and upserts a
URL count into MySQL. The prologue's replay promise shows up in 20 lines of code.

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
docker compose -f layer1/docker-compose.yml up --build
```

The app waits for both Kafka and MySQL health checks before starting.

Send events:

```bash
./layer1/scripts/send-events.sh
```

Check counts:

```bash
curl http://localhost:8080/counts
```

Single event:

```bash
curl -X POST http://localhost:8080/track \
  -H "Content-Type: application/json" \
  -d '{"event_type":"page_view","user_id":"u-1842","url":"/pricing"}'
```

## Test

```bash
# from companion/
./gradlew :layer1:test
```

The repository integration test (`PageCountRepositoryTest`) is currently disabled —
see the comment in that file for why.

## Inspect the topic

```bash
# list events as they land
docker compose -f layer1/docker-compose.yml exec kafka \
  kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic events --from-beginning

# describe topic (1 partition, offset counter)
docker compose -f layer1/docker-compose.yml exec kafka \
  kafka-topics --bootstrap-server localhost:9092 --describe --topic events
```

## Reproduce the heartbeat-inflation bug

The post describes adding a heartbeat event type that inflated page counts because
the consumer counted everything. The repo ships with the fix (`event_type == "page_view"`
filter). To reproduce:

1. Remove the `if ("page_view"...)` guard in `PageCounter.java`.
2. `docker compose -f layer1/docker-compose.yml up --build app`
3. `./layer1/scripts/send-events.sh` — the 20 heartbeats inflate the numbers.
4. Restore the fix, then replay from offset 0:

```bash
./layer1/scripts/replay-demo.sh

# truncate wrong counts first
mysql -h 127.0.0.1 -u analytics -panalytics analytics -e 'TRUNCATE page_counts;'
docker compose -f layer1/docker-compose.yml restart app
```

The consumer re-reads every retained event and recomputes correct counts.
This is the replay property — the code was wrong, the data was not.

## What's next

Layer 2 adds partitions and keys to handle 40x traffic. The key choice — `session_id` —
is the first time bomb.
