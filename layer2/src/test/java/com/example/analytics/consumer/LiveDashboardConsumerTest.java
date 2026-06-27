package com.example.analytics.consumer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveDashboardConsumerTest {

    private static final String PAGE_VIEW =
        "{\"event_type\":\"page_view\",\"session_id\":\"sess-1\",\"user_id\":null,\"url\":\"/pricing\",\"ts\":1000}";

    LiveDashboardConsumer consumer = new LiveDashboardConsumer();

    @Test
    void storesEventInRecent() throws Exception {
        consumer.onEvent(PAGE_VIEW);
        assertThat(consumer.recentSize()).isEqualTo(1);
    }

    @Test
    void boundsRecentEvents() throws Exception {
        for (int i = 0; i < 60; i++) {
            consumer.onEvent(PAGE_VIEW);
        }
        assertThat(consumer.recentSize()).isLessThanOrEqualTo(50);
    }
}
