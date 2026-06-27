package com.example.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageEvent(
    @JsonProperty("event_type") String eventType,
    @JsonProperty("session_id") String sessionId,
    @JsonProperty("user_id") String userId,
    String url,
    long ts
) {}
