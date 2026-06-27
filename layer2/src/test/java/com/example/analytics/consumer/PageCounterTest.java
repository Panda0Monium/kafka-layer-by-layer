package com.example.analytics.consumer;

import com.example.analytics.repository.PageCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PageCounterTest {

    @Mock
    PageCountRepository repo;

    @InjectMocks
    PageCounter counter;

    @Test
    void countsPageViews() throws Exception {
        counter.onEvent("{\"event_type\":\"page_view\",\"session_id\":\"sess-1\",\"user_id\":null,\"url\":\"/pricing\",\"ts\":1000}");
        verify(repo).upsertCount("/pricing");
    }

    @Test
    void ignoresSignupEvents() throws Exception {
        counter.onEvent("{\"event_type\":\"signup\",\"session_id\":\"sess-1\",\"user_id\":\"u-1\",\"url\":\"/signup\",\"ts\":1000}");
        verifyNoInteractions(repo);
    }

    @Test
    void ignoresOtherEventTypes() throws Exception {
        counter.onEvent("{\"event_type\":\"heartbeat\",\"session_id\":\"sess-1\",\"user_id\":null,\"url\":\"\",\"ts\":1000}");
        verifyNoInteractions(repo);
    }
}
