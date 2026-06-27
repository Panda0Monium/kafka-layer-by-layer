#!/usr/bin/env bash
# Shows consumer lag across all three groups reading the events topic.
# Run from inside the Kafka container:
#   docker compose -f layer2/docker-compose.yml exec kafka bash
#   ./scripts/check-lag.sh   (or just paste the command below)

kafka-consumer-groups --bootstrap-server localhost:9092 --describe --all-groups
