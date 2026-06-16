package com.example.analytics.controller;

import com.example.analytics.model.PageCount;
import com.example.analytics.producer.TrackingService;
import com.example.analytics.repository.PageCountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackController.class)
class TrackControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TrackingService trackingService;

    @MockitoBean
    PageCountRepository repo;

    @Test
    void trackReturns202() throws Exception {
        mvc.perform(post("/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event_type\":\"page_view\",\"user_id\":\"u-1\",\"url\":\"/pricing\"}"))
            .andExpect(status().isAccepted());

        verify(trackingService).track("page_view", "u-1", "/pricing");
    }

    @Test
    void countsReturnsList() throws Exception {
        when(repo.findAll()).thenReturn(List.of(new PageCount("/pricing", 42L)));

        mvc.perform(get("/counts"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].url").value("/pricing"))
            .andExpect(jsonPath("$[0].count").value(42));
    }
}
