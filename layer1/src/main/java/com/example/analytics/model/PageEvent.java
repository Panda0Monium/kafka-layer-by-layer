package com.example.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageEvent(
    @JsonProperty("event_type") String eventType,
    @JsonProperty("user_id") String userId,
    String url,
    long ts
) {}
