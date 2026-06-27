package com.example.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrackRequest(
    @JsonProperty("event_type") String eventType,
    @JsonProperty("session_id") String sessionId,
    @JsonProperty("user_id") String userId,
    String url
) {}
