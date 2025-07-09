package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.service.SlideDeckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = SlideDeckController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class SlideDeckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SlideDeckService slideDeckService;

    @Autowired
    private ObjectMapper objectMapper;

    private SlideDeck slideDeck;

    @BeforeEach
    void setUp() {
        slideDeck = new SlideDeck();
        slideDeck.setName("Test Deck");
        slideDeck.setTransitionTime(5);
        slideDeck.setVersion(1);
    }

    @Test
    void testGetAllSlideDecks() throws Exception {
        when(slideDeckService.getAllSlideDecks()).thenReturn(Collections.singletonList(slideDeck));
        mockMvc.perform(get("/api/slidedecks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Deck"));
    }

    @Test
    void testGetSlideDeckById() throws Exception {
        when(slideDeckService.getSlideDeckById(1L)).thenReturn(Optional.of(slideDeck));
        mockMvc.perform(get("/api/slidedecks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testCreateSlideDeck() throws Exception {
        when(slideDeckService.createSlideDeck(any(SlideDeck.class))).thenReturn(slideDeck);
        mockMvc.perform(post("/api/slidedecks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slideDeck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testUpdateSlideDeck() throws Exception {
        when(slideDeckService.updateSlideDeck(any(Long.class), any(SlideDeck.class))).thenReturn(slideDeck);
        mockMvc.perform(put("/api/slidedecks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slideDeck)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Deck"));
    }

    @Test
    void testDeleteSlideDeck() throws Exception {
        mockMvc.perform(delete("/api/slidedecks/1"))
                .andExpect(status().isOk());
    }
} 