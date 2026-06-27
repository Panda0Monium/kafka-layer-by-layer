package com.example.analytics.consumer;

import com.example.analytics.model.PageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LiveDashboardConsumer {

    private static final int MAX_RECENT = 50;

    private final ObjectMapper json = new ObjectMapper();
    private final Deque<PageEvent> recent = new ConcurrentLinkedDeque<>();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "events", groupId = "live-dashboard")
    public void onEvent(String value) throws JsonProcessingException {
        var event = json.readValue(value, PageEvent.class);
        if (recent.size() >= MAX_RECENT) {
            recent.pollFirst();
        }
        recent.addLast(event);
        push(event);
    }

    public SseEmitter subscribe() {
        var emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        try {
            for (PageEvent e : recent) {
                emitter.send(SseEmitter.event().data(e));
            }
        } catch (IOException ex) {
            emitter.completeWithError(ex);
            emitters.remove(emitter);
        }
        return emitter;
    }

    int recentSize() {
        return recent.size();
    }

    private void push(PageEvent event) {
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(event));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        emitters.removeAll(dead);
    }
}
