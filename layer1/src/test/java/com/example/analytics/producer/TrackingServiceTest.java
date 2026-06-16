package com.example.analytics.producer;

import com.example.analytics.model.PageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    KafkaTemplate<String, String> kafka;

    @InjectMocks
    TrackingService service;

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void sendsToEventsTopic() throws Exception {
        service.track("page_view", "u-1", "/pricing");

        var payload = ArgumentCaptor.forClass(String.class);
        verify(kafka).send(eq("events"), payload.capture());

        var event = json.readValue(payload.getValue(), PageEvent.class);
        assertThat(event.eventType()).isEqualTo("page_view");
        assertThat(event.userId()).isEqualTo("u-1");
        assertThat(event.url()).isEqualTo("/pricing");
        assertThat(event.ts()).isPositive();
    }

    @Test
    void timestampIsCurrentTime() throws Exception {
        long before = System.currentTimeMillis();
        service.track("page_view", "u-1", "/home");
        long after = System.currentTimeMillis();

        var payload = ArgumentCaptor.forClass(String.class);
        verify(kafka).send(eq("events"), payload.capture());

        var event = json.readValue(payload.getValue(), PageEvent.class);
        assertThat(event.ts()).isBetween(before, after);
    }
}
