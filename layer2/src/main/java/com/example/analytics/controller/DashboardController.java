package com.example.analytics.controller;

import com.example.analytics.consumer.LiveDashboardConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class DashboardController {

    private final LiveDashboardConsumer dashboard;

    public DashboardController(LiveDashboardConsumer dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/dashboard")
    public SseEmitter stream() {
        return dashboard.subscribe();
    }
}
