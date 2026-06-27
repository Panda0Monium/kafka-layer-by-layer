package com.example.analytics.consumer;

import com.example.analytics.model.PageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SignupAlertsConsumer {

    private static final Logger log = LoggerFactory.getLogger(SignupAlertsConsumer.class);

    private final ObjectMapper json = new ObjectMapper();
    private final AtomicLong signupCount = new AtomicLong();

    @KafkaListener(topics = "events", groupId = "signup-alerts")
    public void onEvent(String value) throws JsonProcessingException {
        var event = json.readValue(value, PageEvent.class);
        if ("signup".equals(event.eventType())) {
            long total = signupCount.incrementAndGet();
            log.info("signup from session={} total={}", event.sessionId(), total);
        }
    }

    public long signupCount() {
        return signupCount.get();
    }
}
