#!/usr/bin/env bash
# Sends session-keyed events to demonstrate per-session ordering and multiple consumer groups.
# Usage: ./scripts/send-events.sh [base_url]
#
# What to observe:
#   - Page counts update at: curl http://localhost:8080/counts
#   - Live dashboard SSE stream: curl -N http://localhost:8080/dashboard
#   - Signup alerts appear in app logs when signup events arrive

BASE="${1:-http://localhost:8080}"

SESSION_A="sess-a1b2c3"
SESSION_B="sess-d4e5f6"
SESSION_C="sess-g7h8i9"

echo "Session A: anonymous browse leading to signup..."
for page in /home /features /pricing; do
  curl -s -X POST "$BASE/track" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"page_view\",\"session_id\":\"$SESSION_A\",\"url\":\"$page\"}" > /dev/null
done
curl -s -X POST "$BASE/track" \
  -H "Content-Type: application/json" \
  -d "{\"event_type\":\"signup\",\"session_id\":\"$SESSION_A\",\"user_id\":\"u-new-1\",\"url\":\"/signup\"}" > /dev/null

echo "Session B: docs browse (no signup)..."
for page in /docs /docs/quickstart /docs/api; do
  curl -s -X POST "$BASE/track" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"page_view\",\"session_id\":\"$SESSION_B\",\"url\":\"$page\"}" > /dev/null
done

echo "Session C: no session_id supplied (server generates UUID key)..."
for page in /blog /pricing; do
  curl -s -X POST "$BASE/track" \
    -H "Content-Type: application/json" \
    -d "{\"event_type\":\"page_view\",\"url\":\"$page\"}" > /dev/null
done

echo "Done."
echo "  Counts:    curl $BASE/counts"
echo "  Dashboard: curl -N $BASE/dashboard"
