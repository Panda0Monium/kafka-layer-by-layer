package com.example.analytics.consumer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignupAlertsConsumerTest {

    SignupAlertsConsumer consumer = new SignupAlertsConsumer();

    @Test
    void countsSignupEvents() throws Exception {
        consumer.onEvent("{\"event_type\":\"signup\",\"session_id\":\"sess-1\",\"user_id\":\"u-1\",\"url\":\"/signup\",\"ts\":1000}");
        consumer.onEvent("{\"event_type\":\"signup\",\"session_id\":\"sess-2\",\"user_id\":\"u-2\",\"url\":\"/signup\",\"ts\":1001}");
        assertThat(consumer.signupCount()).isEqualTo(2);
    }

    @Test
    void ignoresNonSignupEvents() throws Exception {
        consumer.onEvent("{\"event_type\":\"page_view\",\"session_id\":\"sess-1\",\"user_id\":null,\"url\":\"/pricing\",\"ts\":1000}");
        assertThat(consumer.signupCount()).isEqualTo(0);
    }
}
