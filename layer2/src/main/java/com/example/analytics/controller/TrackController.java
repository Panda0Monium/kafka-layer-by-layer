package com.example.analytics.controller;

import com.example.analytics.model.PageCount;
import com.example.analytics.model.TrackRequest;
import com.example.analytics.producer.TrackingService;
import com.example.analytics.repository.PageCountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackController {

    private final TrackingService tracking;
    private final PageCountRepository repo;

    public TrackController(TrackingService tracking, PageCountRepository repo) {
        this.tracking = tracking;
        this.repo = repo;
    }

    @PostMapping("/track")
    public ResponseEntity<Void> track(@RequestBody TrackRequest req) throws JsonProcessingException {
        tracking.track(req.eventType(), req.sessionId(), req.userId(), req.url());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/counts")
    public List<PageCount> counts() {
        return repo.findAll();
    }
}
