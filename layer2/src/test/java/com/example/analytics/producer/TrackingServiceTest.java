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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    KafkaTemplate<String, String> kafka;

    @InjectMocks
    TrackingService service;

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void usesSessionIdAsPartitionKey() throws Exception {
        service.track("page_view", "sess-abc", "u-1", "/pricing");

        var key = ArgumentCaptor.forClass(String.class);
        var payload = ArgumentCaptor.forClass(String.class);
        verify(kafka).send(org.mockito.ArgumentMatchers.eq("events"), key.capture(), payload.capture());

        assertThat(key.getValue()).isEqualTo("sess-abc");
        var event = json.readValue(payload.getValue(), PageEvent.class);
        assertThat(event.sessionId()).isEqualTo("sess-abc");
        assertThat(event.eventType()).isEqualTo("page_view");
        assertThat(event.url()).isEqualTo("/pricing");
    }

    @Test
    void generatesUuidKeyWhenSessionIdAbsent() throws Exception {
        service.track("page_view", null, null, "/home");

        var key = ArgumentCaptor.forClass(String.class);
        verify(kafka).send(org.mockito.ArgumentMatchers.eq("events"), key.capture(), org.mockito.ArgumentMatchers.anyString());

        assertThat(key.getValue()).isNotNull().isNotBlank();
    }

    @Test
    void timestampIsCurrentTime() throws Exception {
        long before = System.currentTimeMillis();
        service.track("page_view", "sess-1", null, "/home");
        long after = System.currentTimeMillis();

        var payload = ArgumentCaptor.forClass(String.class);
        verify(kafka).send(org.mockito.ArgumentMatchers.eq("events"), org.mockito.ArgumentMatchers.anyString(), payload.capture());

        var event = json.readValue(payload.getValue(), PageEvent.class);
        assertThat(event.ts()).isBetween(before, after);
    }
}
