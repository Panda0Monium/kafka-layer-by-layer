#!/usr/bin/env bash
# Sends a mix of page_view and heartbeat events to the ingest endpoint.
# Usage: ./scripts/send-events.sh [base_url]
#
# To reproduce the heartbeat-inflation bug:
#   1. Remove the event_type check in PageCounter.java
#   2. Rebuild: docker compose up --build app
#   3. Run this script -- the 20 heartbeats will inflate /counts
#   4. Reset and replay: ./scripts/replay-demo.sh

BASE="${1:-http://localhost:8080}"
USERS=(u-1842 u-2091 u-3314 u-0042)
PAGES=(/pricing /features /docs /blog /home)

echo "Sending 50 page_view events..."
for i in $(seq 1 50); do
  USER=${USERS[$((RANDOM % ${#USERS[@]}))]}
  PAGE=${PAGES[$((RANDOM % ${#PAGES[@]}))]}
  curl -s -X POST "$BASE/track" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"page_view\",\"user_id\":\"$USER\",\"url\":\"$PAGE\"}" > /dev/null
done

echo "Sending 20 heartbeat events (should NOT appear in counts)..."
for i in $(seq 1 20); do
  USER=${USERS[$((RANDOM % ${#USERS[@]}))]}
  curl -s -X POST "$BASE/track" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"heartbeat\",\"user_id\":\"$USER\",\"url\":\"\"}" > /dev/null
done

echo "Done. Check: curl $BASE/counts"
