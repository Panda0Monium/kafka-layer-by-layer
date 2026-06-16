package com.example.analytics.producer;

import com.example.analytics.model.PageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper json = new ObjectMapper();

    public TrackingService(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    public void track(String eventType, String userId, String url) throws JsonProcessingException {
        var event = new PageEvent(eventType, userId, url, System.currentTimeMillis());
        kafka.send("events", json.writeValueAsString(event));
    }
}
