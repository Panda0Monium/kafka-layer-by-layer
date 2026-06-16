#!/usr/bin/env bash
# Resets the page-counter consumer group to offset 0, triggering full replay.
# Run this after fixing a consumer bug to recompute counts from retained events.
#
# Requires kafka-consumer-groups.sh on PATH, or run from inside the kafka container:
#   docker compose exec kafka kafka-consumer-groups \
#     --bootstrap-server localhost:9092 --group page-counter \
#     --topic events --reset-offsets --to-earliest --execute

BOOTSTRAP="${1:-localhost:9092}"

echo "Resetting page-counter to earliest on topic 'events'..."
kafka-consumer-groups.sh --bootstrap-server "$BOOTSTRAP" \
  --group page-counter --topic events \
  --reset-offsets --to-earliest --execute

echo ""
echo "Next steps:"
echo "  psql -h localhost -U analytics -d analytics -c 'TRUNCATE page_counts;'"
echo "  docker compose restart app"
