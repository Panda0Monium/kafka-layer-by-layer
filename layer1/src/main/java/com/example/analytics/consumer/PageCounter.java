package com.example.analytics.consumer;

import com.example.analytics.model.PageEvent;
import com.example.analytics.repository.PageCountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PageCounter {

    private final ObjectMapper json = new ObjectMapper();
    private final PageCountRepository repo;

    public PageCounter(PageCountRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "events", groupId = "page-counter")
    public void onEvent(String value) throws JsonProcessingException {
        var event = json.readValue(value, PageEvent.class);
        if ("page_view".equals(event.eventType())) {
            repo.upsertCount(event.url());
        }
    }
}
